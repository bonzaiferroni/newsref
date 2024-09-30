package newsref.krawly.utils

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import kotlinx.datetime.Clock
import newsref.db.models.NewsArticle
import newsref.model.dto.SourceInfo
import kotlinx.datetime.Instant
import newsref.db.serializers.readArrayOrObject
import newsref.db.utils.cacheResource
import newsref.db.utils.cacheSerializable
import newsref.db.utils.tryParse
import newsref.model.data.*
import newsref.model.dto.LinkInfo

fun Doc?.readFromLead(lead: Lead): SourceInfo {
    return this.scanElements(lead, this?.allElements)
}

fun Doc?.scanElements(lead: Lead, elements: List<DocElement>?): SourceInfo {
    val contents = mutableSetOf<String>()
    val links = mutableListOf<LinkInfo>()
    var newsArticle = this?.findFirstOrNull("script#json-schema")?.html?.readNewsArticle(lead.url)
    var h1Title: String? = null
    var wordCount = 0
    elements?.forEach {
        if (newsArticle == null && it.tagName == "script" && it.html.contains("NewsArticle")) {
            newsArticle = it.html.readNewsArticle(lead.url)
        }
        if (it.isLinkContent()) return@forEach
        if (it.isHeading()) {
            if (h1Title == null && it.tagName == "h1") {
                h1Title = it.text
            }
        }
        if (it.isContent()) {
            contents.add(it.text)
            wordCount += it.text.wordCount()
            it.eachLink.forEach { (text, url) ->
                links.add(LinkInfo(url = url, urlText = text, context = it.text))
            }
        }
    }
    if (newsArticle != null) {
        print("📜 ")
    }
    return SourceInfo(
        leadUrl = lead.url,
        outletName = this?.readOutletName() ?: newsArticle?.publisher?.name,
        source = Source(
            url = this?.readUrl() ?: newsArticle?.url ?: lead.url,
            leadTitle = lead.headline,
            type = newsArticle?.let { SourceType.ARTICLE } ?: this?.readType() ?: SourceType.UNKNOWN,
            attemptedAt = Clock.System.now()
        ),
        article = if (this != null) Article(
            headline = this.readHeadline() ?: newsArticle?.headline ?: h1Title ?: this.titleText,
            alternativeHeadline = newsArticle?.alternativeHeadline,
            description = this.readDescription() ?: newsArticle?.description,
            imageUrl = this.readImageUrl() ?: newsArticle?.image?.firstOrNull()?.url,
            section = newsArticle?.articleSection,
            keywords = newsArticle?.keywords,
            wordCount = newsArticle?.wordCount ?: wordCount,
            isFree = newsArticle?.isAccessibleForFree,
            thumbnail = newsArticle?.thumbnailUrl,
            language = newsArticle?.inLanguage,
            commentCount = newsArticle?.commentCount,
            accessedAt = Clock.System.now(),
            publishedAt = this.readPublishedAt() ?: newsArticle?.readPublishedAt(),
            modifiedAt = this.readModifiedAt() ?: newsArticle?.readModifiedAt()
        ) else null,
        contents = contents,
        links = links,
        authors = (this?.readAuthor() ?: newsArticle?.readAuthor())?.let { setOf(it) }
    )
}

private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")
private val contentMarkers = setOf('.', '?', '!', ',')

fun DocElement.isHeading() = tagName in headingTags

fun DocElement.isContent() = (tagName == "p" || tagName == "li") && text.any { it in contentMarkers }

fun DocElement.isLinkContent() =
    this.eachLink.keys.firstOrNull()?.let { it == this.text } ?: false

fun Doc.readMetaContent(vararg propertyValues: String) = propertyValues.firstNotNullOfOrNull {
    var value = this.findFirstOrNull("meta[property=\"$it\"]")?.attributes?.get("content")
    if (value == null)
        value = this.findFirstOrNull("meta[name=\"$it\"]")?.attributes?.get("content")
    value // return
}

fun Doc.findFirstOrNull(cssSelector: String): DocElement? = try {
    this.findFirst(cssSelector)
} catch (e: ElementNotFoundException) {
    null
}

fun String.readNewsArticle(url: String): NewsArticle? = this
    .removePrefix("//<![CDATA[")
    .removeSuffix("//]]>")
    .trim()
    .let {
        this.cacheResource(url, "json", "news_article_raw")
        val article = it.readArrayOrObject()
        if (article == null)
            this.cacheResource(url, "json", "news_article_parsed")
        return article // return
    }

fun Doc.readUrl() = this.readMetaContent("url", "og:url", "twitter:url")
fun Doc.readHeadline() = this.readMetaContent("title", "og:title", "twitter:title")
fun Doc.readDescription() = this.readMetaContent("description", "og:description", "twitter:description")
fun Doc.readImageUrl() = this.readMetaContent("image", "og:image", "twitter:image")
fun Doc.readOutletName() = this.readMetaContent("site", "og:site_name", "twitter:site")
fun Doc.readType() = this.readMetaContent("type", "og:type")?.let { SourceType.fromMeta(it) } ?: SourceType.UNKNOWN
fun Doc.readAuthor() = this.readMetaContent("author", "article:author", "og:article:author")
fun Doc.readPublishedAt() = this.readMetaContent("date", "article:published_time")?.let { Instant.tryParse(it) }
fun Doc.readModifiedAt() = this.readMetaContent("last-modified", "article:modified_time")?.let { Instant.tryParse(it) }

fun NewsArticle.readPublishedAt() = this.datePublished?.let { Instant.tryParse(it) }
fun NewsArticle.readModifiedAt() = this.dateModified?.let { Instant.tryParse(it) }
fun NewsArticle.readAuthor() = this.author?.firstOrNull()?.name
