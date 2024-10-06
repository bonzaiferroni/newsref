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
	private val console = globalConsole.getHandle("DocReader")

	suspend fun readDoc(job: LeadJob, outlet: Outlet, doc: Doc): DocumentInfo? {
		val newsArticle = doc.getNewsArticle(job.url)
		console.logDebug("NewsArticle ${job.url.host}: ${if (newsArticle != null) "??" else "null"}")

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
		console.logDebug("found ${links.size} links")
		val urlString = newsArticle?.url ?: doc.readUrl()
		val docUrl = urlString?.toCheckedWithContextOrNull(outlet, sourceUrl)
		val imageUrlString = newsArticle?.image?.firstOrNull()?.url ?: doc.readImageUrl()
		val imageUrl = imageUrlString?.toCheckedWithContextOrNull(outlet, sourceUrl)
		val outletName = newsArticle?.publisher?.name ?: doc.readOutletName()

		val docInfo = DocumentInfo(
			docUrl = docUrl,
			outletId = outlet.id,
			outletName = outletName,
			article = Article(
				headline = doc.readHeadline() ?: newsArticle?.headline ?: h1Title ?: doc.titleText,
				alternativeHeadline = newsArticle?.alternativeHeadline,
				description = newsArticle?.description ?: doc.readDescription(),
				imageUrl = imageUrl,
				section = newsArticle?.articleSection?.firstOrNull(),
				keywords = newsArticle?.keywords,
				wordCount = newsArticle?.wordCount ?: wordCount,
				isFree = newsArticle?.isAccessibleForFree,
				thumbnail = newsArticle?.thumbnailUrl,
				language = newsArticle?.inLanguage,
				commentCount = newsArticle?.commentCount,
				accessedAt = Clock.System.now(),
				publishedAt = newsArticle?.readPublishedAt() ?: doc.readPublishedAt(),
				modifiedAt = newsArticle?.readModifiedAt() ?: doc.readModifiedAt()
			),
			contents = contents,
			links = links,
			authors = (newsArticle?.readAuthor() ?: doc.readAuthor())?.let { setOf(it) },
			type = newsArticle?.let { SourceType.ARTICLE }
				?: doc.readType() ?: SourceType.UNKNOWN,
		)

		if (docInfo.contents.isNotEmpty()) {
			val md = docInfo.toMarkdown()
			md.cacheResource(job.url, "md")
		}

		return docInfo
	}
}