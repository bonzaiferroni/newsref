package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.log.toCyan
import newsref.db.services.ContentService
import newsref.db.utils.cacheResource
import newsref.krawly.utils.*
import newsref.model.core.*
import newsref.model.data.*
import newsref.db.models.PageInfo
import newsref.db.models.LinkInfo

class PageReader(
	private val hostAgent: HostAgent,
	private val contentService: ContentService = ContentService(),
	// private val articleService: ArticleService = ArticleService()
) {
	private val console = globalConsole.getHandle("PageReader", true)
	private var docCount = 0
	private var articleCount = 0

	suspend fun read(lead: LeadInfo, result: WebResult): PageInfo? {
		val doc = result.doc ?: return null

		val pageUrl = result.pageHref?.toUrlOrNull() ?: throw IllegalArgumentException("pageUrl is null")
		val (pageHost, pageHostUrl) = pageUrl.let { hostAgent.getHost(it) }

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
					val (linkHost, linkUrl) = hostAgent.getHost(url)
					val isSibling = linkUrl.isMaybeSibling(pageUrl)
					if (linkHost.apex == pageHost.apex && !isSibling) continue
					links.add(LinkInfo(url = linkUrl, anchorText = text, context = element.text))
				}
			}
		}

		val imageUrlString = newsArticle?.image?.firstOrNull()?.url ?: doc.readImageUrl()
		val imageUrl = imageUrlString?.toUrlWithContextOrNull(pageUrl)
		val hostName = newsArticle?.publisher?.name ?: doc.readHostName()
		val headline = doc.readHeadline() ?: newsArticle?.headline ?: h1Title ?: doc.titleText
		val description = newsArticle?.description ?: doc.readDescription()
		val publishedAt = newsArticle?.readPublishedAt() ?: doc.readPublishedAt()
		val modifiedAt = newsArticle?.readModifiedAt() ?: doc.readModifiedAt()
		val authors = (newsArticle?.readAuthor() ?: doc.readAuthor())?.let { setOf(it) }
		val sourceType = newsArticle?.let { SourceType.ARTICLE } ?: doc.readType() ?: SourceType.UNKNOWN
		wordCount = newsArticle?.wordCount ?: wordCount
		val externalLinkCount = links.count { link -> pageHost.domains.all { link.url.domain != it } }
		val junkParams = cannonUrl?.takeIf { lead.url.apex == it.apex }
			?.let { lead.url.params.keys.toSet() - it.params.keys.toSet() }

		junkParams?.takeIf { it.isNotEmpty() }?.let { console.logDebug("junk params: $junkParams") }

		console.logIfTrue("🖼️") { imageUrl != null }
		console.logIfTrue("📝") { description != null }
		console.logIfTrue("📅") { publishedAt != null }
		console.logIfTrue("🦦") { authors != null }
		console.logIfTrue("📰") { sourceType != SourceType.UNKNOWN }
		console.logIfTrue("${wordCount.toString().toCyan()} words", 9)
		console.logIfTrue("${links.size.toString().toCyan()} links", 9)
		console.logIfTrue("${externalLinkCount.toString().toCyan()} ext", 9)

		if (sourceType == SourceType.ARTICLE) articleCount++
		docCount++
		console.status = "$articleCount/$docCount"

		console.finishPartial()

		val page = PageInfo(
			pageUrl = pageHostUrl,
			hostId = pageHost.id,
			hostName = hostName,
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
			junkParams = junkParams,
		)

		if (page.contents.isNotEmpty()) {
			val md = page.toMarkdown()
			md.cacheResource(pageUrl, "md")
		}

		return page
	}
}
