package newsref.krawly

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import newsref.model.core.Article
import newsref.model.core.InfoSource

fun read(url: String): Article? {
    val document = getDocumentByUrl(url)
    // val content = document.html.tryQuery("div#article-content")
    return document.readByElements(url)
}

fun Doc.readByElements(url: String): Article? {
    val sb = StringBuilder()
    val links = mutableListOf<InfoSource>()

    this.allElements.forEach {
        if (it.isContent()) {
            sb.append(it.text)
            sb.append('\n')
            sb.append('\n')
            it.eachLink.forEach { (link, text) ->
                links.add(InfoSource(it.text, text, link))
            }
        }
    }
    return Article(
        id = 0,
        title = this.titleText,
        description = "",
        url = url,
        imageUrl = "",
        publishedAt = "",
        content = sb.toString(),
        sources = links
    )
}

private val headerTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

fun DocElement.isContent() = (tagName == "p" || tagName in headerTags) && !isLinkContent()

fun DocElement.isLinkContent() =
    this.eachLink.keys.firstOrNull()?.let { it == this.text} ?: false
