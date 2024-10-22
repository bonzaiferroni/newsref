package newsref.krawly.agents

import it.skrape.selects.DocElement
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.log.forestNightBg
import newsref.db.services.ContentService
import newsref.krawly.utils.*
import newsref.model.core.*
import newsref.model.data.*
import newsref.db.models.PageInfo
import newsref.db.models.FetchLinkInfo
import newsref.krawly.models.NewsArticle

class PageReader(
	spindex: Int,
	private val hostAgent: HostAgent,
	private val elementReader: ElementReader = ElementReader(),
	private val contentService: ContentService = ContentService(),
	// private val articleService: ArticleService = ArticleService()
) {
	private val console = globalConsole.getHandle("Page $spindex")
	private var docCount = 0
	private var articleCount = 0

	suspend fun read(lead: LeadInfo, result: WebResult): PageInfo? {
		val doc = result.doc ?: return null

		val pageUrl = result.pageHref?.toUrlOrNull() ?: return null
		val (pageHost, pageHostUrl) = pageUrl.let { hostAgent.getHost(it) }

		val newsArticle = doc.getNewsArticle(pageUrl)

		val cannonHref = newsArticle?.url ?: doc.readCannonHref()
		val cannonUrl = cannonHref?.toUrlOrNull()

		val contents = mutableSetOf<String>()
		val linkHrefs = mutableSetOf<String>()
		val links = mutableListOf<FetchLinkInfo>()
		// var newsArticle = this
		var h1Title: String? = null
		var wordCount = 0
		val typeSets = mutableMapOf(
			ArticleType.NEWS to 0,
			ArticleType.HELP to 0,
			ArticleType.POLICY to 0,
			ArticleType.JOURNAL to 0,
		)
		val stack = ArrayDeque<DocElement>()
		stack.addAll(doc.children.reversed())

		while (stack.isNotEmpty()) {
			val element = stack.removeLastOrNull() ?: break
			val tag = element.tagName

			if (tag in lassoTags) stack.clear()
			if (element.children.isNotEmpty() && tag !in notParentTags) {
				stack.addAll(element.children.reversed())
				continue
			}

			if (element.isHeading()) {
				if (h1Title == null && element.tagName == "h1") {
					h1Title = element.text
				}
			}

			if (tag !in contentTags) continue

			val content = elementReader.read(element) ?: continue
			val cacheContent = content.text.length < 1000
			if (cacheContent) {
				contents.add(content.text)
				if (!contentService.isFresh(content.text)) continue
			}

			wordCount += content.wordCount
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
				val isExternal = linkHost.core != pageHost.core &&
						(linkHost.nexusId == null || linkHost.nexusId != pageHost.nexusId)
				if (!isExternal && !isSibling) continue
				val info = FetchLinkInfo(
					url = linkUrl,
					anchorText = text,
					context = if (cacheContent) content.text else null,
					isExternal = isExternal
				)
				links.add(info)
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
		val articleType = typeSets.maxByOrNull { it.value }?.key ?: ArticleType.UNKNOWN
		val docType = doc.readType()
		wordCount = newsArticle?.wordCount ?: wordCount
		val sourceType = getSourceType(newsArticle, articleType, docType ?: SourceType.UNKNOWN, wordCount)
		val language = newsArticle?.inLanguage ?: doc.readLanguage()
		val junkParams = cannonUrl?.takeIf { lead.url.core == it.core }
			?.let { lead.url.params.keys.toSet() - it.params.keys.toSet() }

		junkParams?.takeIf { it.isNotEmpty() }?.let { console.logDebug("junk params: $junkParams") }

		if (sourceType == SourceType.ARTICLE) articleCount++
		docCount++
		console.status = "$articleCount/$docCount"

		val page = PageInfo(
			pageUrl = pageHostUrl,
			articleType = articleType,
			hostId = pageHost.id,
			hostName = hostName,
			language = language,
			foundNewsArticle = newsArticle != null,
			article = Article(
				headline = headline,
				alternativeHeadline = newsArticle?.alternativeHeadline,
				description = description,
				cannonUrl = cannonUrl?.toString(),
				imageUrl = imageUrl?.toString(),
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