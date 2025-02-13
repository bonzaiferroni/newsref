package newsref.krawly.utils

import com.fleeksoft.ksoup.nodes.Element
import it.skrape.selects.DocElement

fun DocElement.tryGetHrefOrParent(steps: Int = 3): String? {
    if (steps == 0) return null
    if (this.tagName != "a") return this.tryGetHrefOrParent(steps - 1)
    else return this.attributes["href"]
}

fun Element.tryGetHrefOrParent(steps: Int = 3): String? {
    if (this.tagName() == "a") return this.attribute("href")?.value
    if (steps == 0) return null
    return this.parent()?.tryGetHrefOrParent(steps - 1)
}

fun Element.tryGetHrefOrChild(): String? {
    if (this.tagName() == "a") return this.attribute("href")?.value
    for (child in this.children()) {
        val href = child.tryGetHrefOrChild() ?: continue
        return href
    }
    return null
}

private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

fun DocElement.isHeading() = tagName in headingTags

val contentMarkers = setOf('.', '?', '!', ',')
const val MAX_PARAGRAPH_CHARACTERS = 2000

fun DocElement.isContent() = (tagName == "p" || tagName == "li")
        && text.any { it in contentMarkers }
        && text.length < MAX_PARAGRAPH_CHARACTERS