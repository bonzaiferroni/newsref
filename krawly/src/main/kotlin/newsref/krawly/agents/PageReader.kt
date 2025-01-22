package newsref.krawly.agents

import it.skrape.selects.DocElement
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.ContentService
import newsref.krawly.utils.*
import newsref.model.core.*
import newsref.model.data.*
import newsref.db.models.PageInfo
import newsref.db.models.PageLink
import newsref.db.models.WebResult
import newsref.db.services.isNewsContent
import newsref.krawly.models.NewsArticle
import newsref.model.dto.PageAuthor

class PageReader(
	private val hostAgent: HostAgent,
	private val elementReader: ElementReader = ElementReader(),
	private val contentService: ContentService = ContentService(),
) {
	private val console = globalConsole.getHandle("PageReader")
	private var docCount = 0
	private var articleCount = 0

	suspend fun read(lead: LeadInfo, result: WebResult, pageUrl: CheckedUrl?, pageHost: Host?): PageInfo? {
		val now = Clock.System.now()
		val doc = result.content?.contentToDoc() ?: return null
		if (pageUrl == null || pageHost == null) return null

		val newsArticle = doc.getNewsArticle(pageUrl)

		val cannonHref = newsArticle?.url ?: doc.readCannonHref()
		val cannonUrl = cannonHref?.toUrlOrNull()

		val contents = mutableSetOf<String>()
		val linkHrefs = mutableSetOf<String>()
		val links = mutableListOf<PageLink>()
		// var newsArticle = this
		var h1Title: String? = null
		var contentWordCount = 0
		val typeSets = mutableMapOf(
			ArticleType.NEWS to 0,
			ArticleType.HELP to 0,
			ArticleType.POLICY to 0,
			ArticleType.JOURNAL to 0,
		)
		val stack = mutableListOf<DocElement>()
		stack.addAll(doc.children.reversed())
		val dropped = mutableListOf<DocElement>()

		while (stack.isNotEmpty()) {
			val element = stack.removeLastOrNull() ?: break
			val tag = element.tagName

			if (tag in lassoTags) {
				if (tag == "article") {
					dropped.addAll(stack)
				}
				stack.clear()
			}
			if (element.children.isNotEmpty() && tag !in notParentTags) {
				stack.addAll(element.children.reversed())
				continue
			}

			if (stack.isEmpty() && contentWordCount == 0) {
				stack.addAll(dropped)
				dropped.clear()
			}

			if (element.isHeading()) {
				if (h1Title == null && element.tagName == "h1") {
					h1Title = element.text.stripHtmlTags().replace("\n", "")
				}
			}

			if (tag !in contentTags) continue

			val content = elementReader.read(element) ?: continue
			val cacheContent = content.text.length < 1000
			if (cacheContent) {
				try {
					if (!contentService.isFresh(content.text)) continue
					contents.add(content.text)
				} catch (e: Exception) {
					console.logError("Exception caching content:\n$pageUrl\n${e.message}")
					continue
				}
			}

			contentWordCount += content.wordCount
			for ((type, score) in typeSets) {
				typeSets[type] = score + (content.typeSets[type] ?: 0)
			}

			for ((text, href) in element.eachLink) {
				if (linkHrefs.contains(href)) continue
				linkHrefs.add(href)

				val url = href.toUrlWithContextOrNull(pageUrl) ?: continue
				if (url.isLikelyAd()) continue
				if (url.isNotWebLink()) continue

				val (linkHost, linkUrl) = hostAgent.getHost(url)
				val isSibling = linkUrl.isMaybeSibling(pageUrl)
				val isExternal = linkHost.isExternalTo(pageHost)
				if (!isExternal && !isSibling) continue
				val info = PageLink(
					url = linkUrl,
					anchorText = text,
					context = if (cacheContent) content.text else null,
					isExternal = isExternal
				)
				links.add(info)
			}
		}

		val imageUrlString = newsArticle?.image?.firstNotNullOfOrNull {
			if (it.width != null && it.width >= 640 && it.width <= 1280) it else null
		}?.url ?: newsArticle?.image?.firstOrNull()?.url ?: doc.readImageUrl()
		val imageUrl = imageUrlString?.toUrlWithContextOrNull(pageUrl)
		val hostName = newsArticle?.publisher?.name ?: doc.readHostName()
		val headline = doc.readHeadline() ?: newsArticle?.headline ?: doc.titleText
		val description = newsArticle?.description ?: doc.readDescription()
		val publishedAt = newsArticle?.readPublishedAt() ?: doc.readPublishedAt()
		val modifiedAt = newsArticle?.readModifiedAt() ?: doc.readModifiedAt()
		val authors = newsArticle?.readAuthor() ?: doc.readAuthor()?.let { listOf(PageAuthor(name = it)) }
		val articleType = typeSets.maxByOrNull { it.value }?.key ?: ArticleType.UNKNOWN
		val docType = doc.readType()
		val sourceType = getSourceType(newsArticle, articleType, docType ?: SourceType.UNKNOWN, contentWordCount)
		val language = newsArticle?.inLanguage ?: doc.readLanguage()
		val thumbnail = newsArticle?.thumbnailUrl ?: newsArticle?.image?.takeIf { it.size > 1 }
			?.firstNotNullOfOrNull{ if (it.width != null && it.width < 640) it else null }?.url

		if (sourceType == SourceType.ARTICLE) articleCount++
		docCount++
		console.status = "$articleCount/$docCount"

		val page = PageInfo(
			source = Source(
				url = pageUrl,
				title = doc.titleText,
				type = sourceType,
				thumbnail = thumbnail,
				imageUrl = imageUrl?.href,
				contentCount = if (isNewsContent(sourceType, language)) contentWordCount else 0,
				seenAt = lead.freshAt ?: now,
				accessedAt = now,
				publishedAt = publishedAt
			),
			pageHost = pageHost,
			articleType = articleType,
			hostName = hostName,
			language = language,
			foundNewsArticle = newsArticle != null,
			article = Article(
				headline = headline,
				alternativeHeadline = newsArticle?.alternativeHeadline,
				description = description,
				cannonUrl = cannonUrl?.toString(),
				section = newsArticle?.articleSection?.firstOrNull(),
				keywords = newsArticle?.keywords,
				wordCount = newsArticle?.wordCount ?: contentWordCount,
				isFree = newsArticle?.isAccessibleForFree,
				language = newsArticle?.inLanguage,
				commentCount = newsArticle?.commentCount,
				modifiedAt = modifiedAt,
			),
			contents = contents,
			links = links,
			authors = authors,
		)

		return page
	}

	private fun getSourceType(
		newsArticle: NewsArticle?,
		articleType: ArticleType,
		sourceType: SourceType,
		wordCount: Int
	): SourceType {
		if (newsArticle != null) return SourceType.ARTICLE
		if (wordCount < 50 || sourceType != SourceType.ARTICLE) return sourceType
		if (articleType == ArticleType.POLICY) return SourceType.WEBSITE
		return sourceType
	}
}

private val contentTags = setOf("p", "li", "span", "blockquote")
private val notParentTags = setOf(
	"p", "h1", "h2", "h3", "h4", "h5", "h6",
	"span", "a", "img", "nav", "head", "header", "footer",
	"form", "input", "button", "label", "textarea",
	"table", "thead", "tbody", "tr", "td", "th",
	"figure", "figcaption", "iframe", "aside",
	"details", "summary", "fieldset", "legend",
	"script", "style", "link", "meta", "svg", "embed"
)
private val lassoTags = setOf("body", "main", "article")

fun SourceType.getEmoji() = when (this) {
	SourceType.ARTICLE -> "📜"
	SourceType.WEBSITE -> "🥱"
	SourceType.IMAGE -> "🌆"
	SourceType.BLOG_POST -> "📝"
	SourceType.VIDEO -> "📼"
	SourceType.SOCIAL_POST -> "👯"
	else -> "🧀"
}

private fun String.stripHtmlTags(): String {
	return this.replace(Regex("<.*?>"), "")
}