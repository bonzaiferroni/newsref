package newsref.krawly.utils

import com.eygraber.uri.Url
import it.skrape.selects.DocElement
import newsref.db.utils.toUrlOrNull

fun DocElement.tryGetUrl(): Pair<String, Url>? = this.eachLink.entries.firstOrNull()?.let { (text, href) ->
    href.toUrlOrNull()?.let { Pair(text, it) }
}

private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

fun DocElement.isHeading() = tagName in headingTags

private val contentMarkers = setOf('.', '?', '!', ',')

fun DocElement.isContent() = (tagName == "p" || tagName == "li") && text.any { it in contentMarkers }

fun DocElement.isLinkContent() =
    this.eachLink.keys.firstOrNull()?.let { it == this.text } ?: false