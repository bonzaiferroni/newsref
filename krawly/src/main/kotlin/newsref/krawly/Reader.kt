package newsref.krawly

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import it.skrape.selects.html5.content
import kotlinx.datetime.Clock
import newsref.db.tables.SourceTable.title
import newsref.model.data.Source
import newsref.model.data.Link
import newsref.model.dto.ArticleInfo

fun read(url: String): ArticleInfo {
    val document = getDocumentByUrl(url)
    // val content = document.html.tryQuery("div#article-content")
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
    var title: String? = null
    elements.forEach {
        if (it.isContent()) {
            if (title == null && it.tagName == "h1") {
                title = it.text
            }
            sb.append(it.text)
            sb.append('\n')
            sb.append('\n')
            it.eachLink.forEach { (text, link) ->
                links.add(Link(url = link, urlText = text, context = it.text))
            }
        }
    }
    return ArticleInfo(
        source = Source(
            title = title ?: this.titleText,
            url = url,
            description = this.readDescription(),
            content = sb.toString(),
            accessedAt = Clock.System.now()
        ),
        links = links
    )
}

private val headerTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

fun DocElement.isContent() = (tagName == "p" || tagName in headerTags) && !isLinkContent()

fun DocElement.isLinkContent() =
    this.eachLink.keys.firstOrNull()?.let { it == this.text } ?: false

fun Doc.readDescription() = this.findFirst("meta[property=\"og:description\"]").attributes["content"]