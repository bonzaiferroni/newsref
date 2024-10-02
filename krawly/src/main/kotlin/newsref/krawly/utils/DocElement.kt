package newsref.krawly.utils

import com.eygraber.uri.Uri
import it.skrape.selects.DocElement
import newsref.db.utils.toUriOrNull

fun DocElement.tryGetHref(): Pair<String, String>? =
    this.eachLink.entries.firstOrNull()?.let { Pair(it.key, it.value) }

private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

fun DocElement.isHeading() = tagName in headingTags

private val contentMarkers = setOf('.', '?', '!', ',')

fun DocElement.isContent() = (tagName == "p" || tagName == "li") && text.any { it in contentMarkers }

fun DocElement.isLinkContent() =
    this.eachLink.keys.firstOrNull()?.let { it == this.text } ?: false