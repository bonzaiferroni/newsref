package newsref.krawly.utils

import it.skrape.selects.DocElement

fun DocElement.tryGetHref(): Pair<String, String>? =
    this.eachLink.entries.firstOrNull()?.let { Pair(it.key, it.value) }

private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

fun DocElement.isHeading() = tagName in headingTags

val contentMarkers = setOf('.', '?', '!', ',')
const val MAX_PARAGRAPH_CHARACTERS = 2000

fun DocElement.isContent() = (tagName == "p" || tagName == "li")
        && text.any { it in contentMarkers }
        && text.length < MAX_PARAGRAPH_CHARACTERS