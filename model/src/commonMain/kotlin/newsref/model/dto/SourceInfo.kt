package newsref.model.dto

import newsref.model.data.Content
import newsref.model.data.Document
import newsref.model.data.Source

data class SourceInfo(
    val leadUrl: String,
    val source: Source,
    val document: Document? = null,
    val outletName: String? = null,
    val contents: List<Content> = emptyList(),
    val links: List<LinkInfo> = emptyList(),
)

data class LinkInfo(
    val url: String,
    val urlText: String,
    val context: String,
)