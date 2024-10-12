package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.log.toCyan
import newsref.db.services.ContentService
import newsref.db.utils.cacheResource
import newsref.krawly.utils.*
import newsref.model.core.*
import newsref.model.data.*
import newsref.model.dto.PageInfo
import newsref.model.dto.LinkInfo

class PageReader(
	private val outletAgent: OutletAgent,
	private val contentService: ContentService = ContentService(),
	// private val articleService: ArticleService = ArticleService()
) {
	private val console = globalConsole.getHandle("PageReader", true)
	private var docCount = 0
	private var articleCount = 0

	suspend fun read(lead: LeadInfo, result: WebResult): PageInfo? {
		val doc = result.doc ?: return null

		val pageUrl = result.pageHref?.toUrlOrNull() ?: throw IllegalArgumentException("pageUrl is null")
		val pageOutlet = pageUrl.let { outletAgent.getOutlet(it) }
		val pageCheckedUrl = pageOutlet.let { pageUrl.href.toCheckedUrl(it) }

		val newsArticle = doc.getNewsArticle(pageUrl)
		console.logIfTrue("📜") { newsArticle != null }

		val cannonHref = newsArticle?.url ?: doc.readCannonHref()
		val cannonUrl = cannonHref?.toUrl()

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
				val content = element.text
				if (!contentService.isFresh(content)) continue
				wordCount += content.wordCount()
				contents.add(content)
				for ((text, href) in element.eachLink) {
					val url = href.toUrlWithContextOrNull(pageUrl) ?: continue
					if (url.isLikelyAd()) continue
					if (url.isNotWebLink()) continue
					val linkOutlet = outletAgent.getOutlet(url)
					val linkUrl = url.toString().toCheckedWithContextOrNull(linkOutlet, pageUrl)
						?: continue
					val isInternal = linkOutlet.isInternalTo(pageOutlet)
					val isSibling = linkUrl.isSibling(pageUrl)
					if (isInternal && !isSibling) continue
					links.add(LinkInfo(url = linkUrl, anchorText = text, context = element.text))
				}
			}
		}

		val imageUrlString = newsArticle?.image?.firstOrNull()?.url ?: doc.readImageUrl()
		val imageUrl = imageUrlString?.toUrlWithContextOrNull(pageUrl)
		console.logIfTrue("🖼️") { imageUrl != null }
		val outletName = newsArticle?.publisher?.name ?: doc.readOutletName()
		val headline = doc.readHeadline() ?: newsArticle?.headline ?: h1Title ?: doc.titleText
		val description = newsArticle?.description ?: doc.readDescription()
		console.logIfTrue("📝") { description != null }
		val publishedAt = newsArticle?.readPublishedAt() ?: doc.readPublishedAt()
		val modifiedAt = newsArticle?.readModifiedAt() ?: doc.readModifiedAt()
		console.logIfTrue("📅") { publishedAt != null }
		val authors = (newsArticle?.readAuthor() ?: doc.readAuthor())?.let { setOf(it) }
		console.logIfTrue("🦦") { authors != null }
		val sourceType = newsArticle?.let { SourceType.ARTICLE } ?: doc.readType() ?: SourceType.UNKNOWN
		console.logIfTrue("📰") { sourceType != SourceType.UNKNOWN }
		wordCount = newsArticle?.wordCount ?: wordCount
		val externalLinkCount = links.count { link -> pageOutlet.domains.all { link.url.host != it } }
		console.logIfTrue("${wordCount.toString().toCyan()} words", 9)
		console.logIfTrue("${links.size.toString().toCyan()} links", 9)
		console.logIfTrue("${externalLinkCount.toString().toCyan()} external") 

		if (sourceType == SourceType.ARTICLE) articleCount++
		docCount++
		console.status = "$articleCount/$docCount"

		console.finishPartial()

		val docInfo = PageInfo(
			leadUrl = lead.url,
			pageUrl = pageCheckedUrl,
			outletId = pageOutlet.id,
			outletName = outletName,
			article = Article(
				headline = headline,
				alternativeHeadline = newsArticle?.alternativeHeadline,
				description = description,
				cannonUrl = cannonUrl,
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
			md.cacheResource(pageUrl, "md")
		}

		return docInfo
	}
}

private fun Outlet.isInternalTo(other: Outlet) = this.domains.map { it.removePrefix("www.").lowercase() }
	.intersect(other.domains.map { it.removePrefix("www.").lowercase() }.toSet())
	.isNotEmpty()

