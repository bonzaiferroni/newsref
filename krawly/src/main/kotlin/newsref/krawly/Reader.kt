package newsref.krawly

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import kotlinx.datetime.Clock
import newsref.model.data.Source
import newsref.model.data.Link
import newsref.model.dto.ArticleInfo

fun read(url: String): ArticleInfo {
    val document = getDocumentByUrl(url)
    // val content = document.html.tryQuery("div#article-content")
    return document.readByElements(url)
}

fun Doc.readByElements(url: String): ArticleInfo {
    return allElements.scanElements(url, this.titleText)
}

fun Doc.readyBySelector(url: String): ArticleInfo {
    return this.findAll("div#article-content").scanElements(url, this.titleText)
}

fun List<DocElement>.scanElements(url: String, title: String): ArticleInfo {
    val sb = StringBuilder()
    val links = mutableListOf<Link>()
    this.forEach {
        if (it.isContent()) {
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
            title = title,
            url = url,
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
