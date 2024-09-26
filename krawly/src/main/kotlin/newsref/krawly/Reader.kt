package newsref.krawly

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import newsref.db.utils.NewsArticle
import newsref.model.data.Source
import newsref.model.data.Link
import newsref.model.dto.ArticleInfo
import newsref.model.utils.removeQueryParameters
import kotlinx.datetime.Instant
import newsref.model.data.Document
import newsref.model.data.SourceType

fun read(url: String): ArticleInfo {
    val document = getDocumentByUrl(url)
        ?: return ArticleInfo(source = Source(url = url))
    return document.readByElements(url)
}

fun Doc.readByElements(url: String): ArticleInfo {
    return this.scanElements(url, allElements)
}

fun Doc.readyBySelector(url: String): ArticleInfo {
    return this.scanElements(url, this.findAll("div#article-content"))
}

fun Doc.scanElements(url: String, elements: List<DocElement>): ArticleInfo {
    val sb = StringBuilder()
    val links = mutableListOf<Link>()
    var h1Title: String? = null
    elements.forEach {
        if (it.isContent()) {
            if (h1Title == null && it.tagName == "h1") {
                h1Title = it.text
            }
            sb.append(it.text)
            sb.append('\n')
            sb.append('\n')
            it.eachLink.forEach { (text, link) ->
                links.add(Link(url = link, urlText = text, context = it.text))
            }
        }
    }
    val newsArticle = this.readNewsArticle()
    return ArticleInfo(
        outletName = this.readOutletName() ?: newsArticle?.publisher?.name,
        source = Source(
            url = this.readUrl() ?: url.removeQueryParameters(),
            type = this.readType()
        ),
        document = Document(
            title = this.readTitle() ?: h1Title ?: this.titleText,
            content = sb.toString(),
            description = this.readDescription(),
            imageUrl = this.readImageUrl(),
            accessedAt = Clock.System.now(),
            publishedAt = newsArticle?.readDatePublished(),
            modifiedAt = newsArticle?.readDateModified()
        ),
        links = links
    )
}

private val headerTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

fun DocElement.isContent() = (tagName == "p" || tagName in headerTags) && !isLinkContent()

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
fun Doc.readTitle() = this.readMetaContent("title", "og:title", "twitter:title")
fun Doc.readDescription() = this.readMetaContent("description", "og:description", "twitter:description")
fun Doc.readImageUrl() = this.readMetaContent("image", "og:image", "twitter:image")
fun Doc.readOutletName() = this.readMetaContent("site", "og:site_name", "twitter:site")
fun Doc.readType() = this.readMetaContent("type", "og:type")?.let { SourceType.fromMeta(it) } ?: SourceType.UNKNOWN

fun NewsArticle.readDatePublished() = this.datePublished.let { Instant.parse(it) }
fun NewsArticle.readDateModified() = this.dateModified.let { Instant.parse(it) }

fun Doc.readNewsArticle() = this.findFirstOrNull("script#json-schema")?.html
    ?.removePrefix("//<![CDATA[")
    ?.removeSuffix("//]]>")
    ?.let {
        try {
            println(it)
            json.decodeFromString<NewsArticle>(it)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

private val json = Json { ignoreUnknownKeys = true }