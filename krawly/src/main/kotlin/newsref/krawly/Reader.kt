package newsref.krawly

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import newsref.model.data.Article
import newsref.model.data.Source

fun read(url: String): Article {
    val document = getDocumentByUrl(url)
    // val content = document.html.tryQuery("div#article-content")
    return document.readByElements(url)
}

fun Doc.readByElements(url: String): Article {
    return allElements.scanElements(url, this.titleText)
}

fun Doc.readyBySelector(url: String): Article {
    return this.findAll("div#article-content").scanElements(url, this.titleText)
}

fun List<DocElement>.scanElements(url: String, title: String): Article {
    val sb = StringBuilder()
    val links = mutableListOf<Source>()
    this.forEach {
        if (it.isContent()) {
            sb.append(it.text)
            sb.append('\n')
            sb.append('\n')
            it.eachLink.forEach { (text, link) ->
                links.add(Source(it.text, text, link))
            }
        }
    }
    return Article(
        id = 0,
        title = title,
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
    this.eachLink.keys.firstOrNull()?.let { it == this.text } ?: false
