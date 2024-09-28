package newsref.krawly

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import newsref.db.utils.NewsArticle
import newsref.model.dto.SourceInfo
import kotlinx.datetime.Instant
import newsref.db.utils.tryParse
import newsref.krawly.utils.wordCount
import newsref.model.data.*
import newsref.model.dto.LinkInfo
import java.io.File

fun read(leadUrl: String): SourceInfo {
    val document = fetch(leadUrl)
    val file = File("dump/lastpage.html")
    file.writeText(document.html)
    println("Reader: reading document")
    return document.readByElements(leadUrl)
}

fun Doc.readByElements(url: String): SourceInfo {
    return this.scanElements(url, allElements)
}

fun Doc.readyBySelector(url: String): SourceInfo {
    return this.scanElements(url, this.findAll("div#article-content"))
}

fun Doc.scanElements(leadUrl: String, elements: List<DocElement>): SourceInfo {
    val contents = mutableSetOf<String>()
    val links = mutableListOf<LinkInfo>()
    var newsArticle = this.findFirstOrNull("script#json-schema")?.readNewsArticle()
    var h1Title: String? = null
    var wordCount = 0
    elements.forEach {
        if (newsArticle == null && it.tagName == "script" && it.html.contains("NewsArticle")) {
            newsArticle = it.readNewsArticle()
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
    println("Reader: NewsArticle: ${newsArticle != null}")
    return SourceInfo(
        leadUrl = leadUrl,
        outletName = this.readOutletName() ?: newsArticle?.publisher?.name,
        source = Source(
            url = this.readUrl() ?: newsArticle?.url ?: leadUrl,
            type = newsArticle?.let { SourceType.ARTICLE } ?: this.readType(),
            attemptedAt = Clock.System.now()
        ),
        article = Article(
            headline = this.readHeadline() ?: newsArticle?.headline ?: h1Title ?: this.titleText,
            alternativeHeadline = newsArticle?.alternativeHeadline,
            description = this.readDescription() ?: newsArticle?.description,
            section = newsArticle?.articleSection,
            keywords = newsArticle?.keywords,
            imageUrl = this.readImageUrl() ?: newsArticle?.image?.firstOrNull()?.url,
            accessedAt = Clock.System.now(),
            publishedAt = this.readPublishedAt() ?: newsArticle?.readPublishedAt(),
            modifiedAt = this.readModifiedAt() ?: newsArticle?.readModifiedAt(),
            wordCount = newsArticle?.wordCount ?: wordCount
        ),
        contents = contents,
        links = links,
        authors = (this.readAuthor() ?: newsArticle?.readAuthor())?.let { setOf(it) }
    )
}

private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")
private val contentMarkers = setOf('.', '?', '!', ',')

fun DocElement.isHeading() = tagName in headingTags

fun DocElement.isContent() = tagName == "p" && text.any { it in contentMarkers }

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

fun DocElement.readNewsArticle() = html
    .removePrefix("//<![CDATA[")
    .removeSuffix("//]]>")
    .trim()
    .let {
        try {
            json.decodeFromString<NewsArticle>(it);
        } catch (e: Exception) {
            println(e)
            println(it)
            null
        }
    }

private val json = Json { ignoreUnknownKeys = true }