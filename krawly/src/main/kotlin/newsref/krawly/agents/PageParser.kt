package newsref.krawly.agents

import it.skrape.selects.DocElement
import kotlinx.datetime.Clock
import newsref.db.core.CheckedUrl
import newsref.db.core.toUrlOrNull
import newsref.db.core.toUrlWithContextOrNull
import newsref.db.globalConsole
import newsref.db.model.Host
import newsref.db.model.LeadInfo
import newsref.db.services.ContentService
import newsref.krawly.utils.*
import newsref.model.core.*
import newsref.db.model.CrawledData
import newsref.db.model.CrawledLink
import newsref.db.model.Page
import newsref.db.model.WebResult
import newsref.db.services.isNewsContent
import newsref.krawly.models.MetaNewsArticle
import newsref.model.dto.CrawledAuthor

private val console = globalConsole.getHandle("PageReader")

class PageParser(
	private val hostAgent: HostAgent,
	private val elementReader: ElementReader = ElementReader(),
	private val contentService: ContentService = ContentService(),
) {
	private val console = globalConsole.getHandle("PageReader")
	private var docCount = 0
	private var articleCount = 0

	suspend fun read(lead: LeadInfo, result: WebResult, pageUrl: CheckedUrl?, pageHost: Host?): CrawledData? {
		val now = Clock.System.now()
		val doc = result.content?.contentToDoc() ?: return null
		if (pageUrl == null || pageHost == null) return null

		val newsArticle = doc.getNewsArticle(pageUrl)

		val cannonHref = newsArticle?.url ?: doc.readCannonHref()
		val cannonUrl = cannonHref?.toUrlOrNull()

		val contents = mutableSetOf<String>()
		val linkHrefs = mutableSetOf<String>()
		val links = mutableListOf<CrawledLink>()
		// var newsArticle = this
		var h1Title: String? = null
		var contentWordCount = 0
		var readWordCount = 0
		val typeSets = mutableMapOf(
			ArticleCategory.News to 0,
			ArticleCategory.Help to 0,
			ArticleCategory.Policy to 0,
			ArticleCategory.Journal to 0,
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

			if (stack.isEmpty() && readWordCount == 0) {
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
			val cacheContent = content.text.length < 2000
			if (cacheContent) {
				try {
					if (!contentService.isFresh(content.text)) continue
					contents.add(content.text)
					contentWordCount += content.wordCount
				} catch (e: Exception) {
					console.logError("Exception caching content:\n$pageUrl\n${e.message}")
					continue
				}
			} else {
				console.log("${pageUrl.core}: un-cache content length: ${content.text.length}")
			}

			readWordCount += readWordCount
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
				val info = CrawledLink(
					url = linkUrl,
					anchorText = text,
					textIndex = element.findLinkTextIndex(href, text, content.text, lead.url),
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
		val authors = newsArticle?.readAuthor() ?: doc.readAuthor()?.let { listOf(CrawledAuthor(name = it)) }
		val articleCategory = typeSets.maxByOrNull { it.value }?.key ?: ArticleCategory.Unknown
		val metaType = doc.readType()
		val contentType = getSourceType(newsArticle, articleCategory, metaType ?: ContentType.Unknown, contentWordCount)
		val language = newsArticle?.inLanguage ?: doc.readLanguage()
		val thumbnail = newsArticle?.thumbnailUrl ?: newsArticle?.image?.takeIf { it.size > 1 }
			?.firstNotNullOfOrNull{ if (it.width != null && it.width < 640) it else null }?.url

		if (contentType == ContentType.NewsArticle) articleCount++
		docCount++
		console.status = "$articleCount/$docCount"

		val page = Page(
			url = pageUrl,
			title = doc.titleText,
			type = contentType,
			thumbnail = thumbnail,
			imageUrl = imageUrl?.href,
			cachedWordCount = if (isNewsContent(contentType, language)) contentWordCount else 0,
			okResponse = true,

			headline = headline,
			alternativeHeadline = newsArticle?.alternativeHeadline,
			description = description,
			cannonUrl = cannonUrl?.toString(),
			metaSection = newsArticle?.articleSection?.firstOrNull(),
			keywords = newsArticle?.keywords,
			wordCount = newsArticle?.wordCount ?: contentWordCount,
			isFree = newsArticle?.isAccessibleForFree,
			language = newsArticle?.inLanguage,
			commentCount = newsArticle?.commentCount,

			seenAt = lead.freshAt ?: now,
			accessedAt = now,
			publishedAt = publishedAt,
			modifiedAt = modifiedAt,
		)
		val crawledData = CrawledData(
			page = page,
			pageHost = pageHost,
			articleCategory = articleCategory,
			hostName = hostName,
			language = language,
			foundNewsArticle = newsArticle != null,
			contents = contents,
			links = links,
			authors = authors,
		)

		return crawledData
	}

	private fun getSourceType(
		metaNewsArticle: MetaNewsArticle?,
		articleCategory: ArticleCategory,
		contentType: ContentType,
		wordCount: Int
	): ContentType {
		if (metaNewsArticle != null) return ContentType.NewsArticle
		if (wordCount < 100 || contentType != ContentType.NewsArticle) return contentType
		if (articleCategory == ArticleCategory.Policy) return ContentType.Website
		return contentType
	}
}

private fun DocElement.findLinkTextIndex(
	href: String,
	text: String,
	context: String,
	leadUrl: CheckedUrl,
): Int {
	val parts = context.split(text)
	if (parts.size == 2) return parts[0].length
	var currentIndex = 0
	val html = this.html
	var i = 0
	while (i < html.length) {
		val char = html[i]
		if (char == '<') {
			val closingBracketIndex = html.indexOf('>', i)
			if (closingBracketIndex < 0) return -1
			val elementHtml = html.substring(i, closingBracketIndex + 1)
			if (elementHtml.length < 3) return -1
			val textEndIndex = currentIndex + text.length
			if (textEndIndex > context.length) return -1
			if (elementHtml[1] == 'a' && elementHtml.contains(href)
				&& context.substring(currentIndex, textEndIndex) == text) {
				return currentIndex
			}
			i = closingBracketIndex + 1
		} else {
			i++
			currentIndex++
		}
	}
	console.logError("${leadUrl.core}: Unable to find index")
	return -1
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

fun ContentType.getEmoji() = when (this) {
	ContentType.NewsArticle -> "📜"
	ContentType.Website -> "🥱"
	ContentType.Image -> "🌆"
	ContentType.BlogPost -> "📝"
	ContentType.Video -> "📼"
	ContentType.SocialPost -> "👯"
	else -> "🧀"
}

private fun String.stripHtmlTags(): String {
	return this.replace(Regex("<.*?>"), "")
}