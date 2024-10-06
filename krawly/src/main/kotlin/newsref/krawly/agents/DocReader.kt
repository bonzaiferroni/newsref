package newsref.krawly.agents

import it.skrape.selects.Doc
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.utils.cacheResource
import newsref.krawly.utils.*
import newsref.model.core.toCheckedOrNull
import newsref.model.core.toCheckedWithContextOrNull
import newsref.model.core.toUrlOrNull
import newsref.model.core.toUrlWithContextOrNull
import newsref.model.data.*
import newsref.model.dto.DocumentInfo
import newsref.model.dto.LinkInfo

class DocReader(
	private val outletAgent: OutletAgent,
	// private val articleService: ArticleService = ArticleService()
) {
	private val console = globalConsole.getHandle("DocReader", true)

	suspend fun readDoc(job: LeadJob, outlet: Outlet, doc: Doc): DocumentInfo? {
		val newsArticle = doc.getNewsArticle(job.url)
		console.logMsgIfTrue("📜") { newsArticle != null }

		val sourceUrl = (newsArticle?.url ?: doc.readUrl())?.toUrlOrNull() ?: job.url
		val sourceOutlet = outletAgent.getOutlet(sourceUrl)                     // <- OutletAgent ->
		val sourceCheckedUrl = sourceUrl.toString().toCheckedOrNull(sourceOutlet) ?: return null

		val contents = mutableSetOf<String>()
		val links = mutableListOf<LinkInfo>()
		// var newsArticle = this
		var h1Title: String? = null
		var wordCount = 0
		for (element in doc.allElements) {
			if (element.isLinkContent()) continue
			if (element.isHeading()) {
				if (h1Title == null && element.tagName == "h1") {
					h1Title = element.text
				}
			}
			if (element.isContent()) {
				contents.add(element.text)
				wordCount += element.text.wordCount()
				for ((text, href) in element.eachLink) {
					val url = href.toUrlWithContextOrNull(sourceCheckedUrl) ?: continue
					if (url.isLikelyAd()) continue
					val linkOutlet = outletAgent.getOutlet(url)
					val checkedUrl = url.toString().toCheckedWithContextOrNull(outlet, sourceCheckedUrl)
						?: continue
					links.add(LinkInfo(url = checkedUrl, anchorText = text, context = element.text))
				}
			}
		}
		val urlString = newsArticle?.url ?: doc.readUrl()
		val docUrl = urlString?.toCheckedWithContextOrNull(outlet, sourceUrl)

		val imageUrlString = newsArticle?.image?.firstOrNull()?.url ?: doc.readImageUrl()
		val imageUrl = imageUrlString?.toCheckedWithContextOrNull(outlet, sourceUrl)
		console.logMsgIfTrue("🖼️") { imageUrl != null }
		val outletName = newsArticle?.publisher?.name ?: doc.readOutletName()
		val headline = doc.readHeadline() ?: newsArticle?.headline ?: h1Title ?: doc.titleText
		val description = newsArticle?.description ?: doc.readDescription()
		console.logMsgIfTrue("📝") { description != null }
		val publishedAt = newsArticle?.readPublishedAt() ?: doc.readPublishedAt()
		val modifiedAt = newsArticle?.readModifiedAt() ?: doc.readModifiedAt()
		console.logMsgIfTrue("📅") { publishedAt != null }
		val authors = (newsArticle?.readAuthor() ?: doc.readAuthor())?.let { setOf(it) }
		console.logMsgIfTrue("🧑‍🏫") { authors != null }
		val sourceType = newsArticle?.let { SourceType.ARTICLE } ?: doc.readType() ?: SourceType.UNKNOWN
		console.logMsgIfTrue("📰") { sourceType != SourceType.UNKNOWN }
		wordCount = newsArticle?.wordCount ?: wordCount
		console.logMsgIfTrue("$wordCount words", 9) { wordCount > 0 }
		console.logMsgIfTrue("${links.size} links", 9) { links.size > 0 }
		console.finishPartial()

		val docInfo = DocumentInfo(
			docUrl = docUrl,
			outletId = outlet.id,
			outletName = outletName,
			article = Article(
				headline = headline,
				alternativeHeadline = newsArticle?.alternativeHeadline,
				description = description,
				imageUrl = imageUrl,
				section = newsArticle?.articleSection?.firstOrNull(),
				keywords = newsArticle?.keywords,
				wordCount = wordCount,
				isFree = newsArticle?.isAccessibleForFree,
				thumbnail = newsArticle?.thumbnailUrl,
				language = newsArticle?.inLanguage,
				commentCount = newsArticle?.commentCount,
				accessedAt = Clock.System.now(),
				publishedAt = publishedAt,
				modifiedAt = modifiedAt,
			),
			contents = contents,
			links = links,
			authors = authors,
			type = sourceType,
		)

		if (docInfo.contents.isNotEmpty()) {
			val md = docInfo.toMarkdown()
			md.cacheResource(job.url, "md")
		}

		return docInfo
	}
}