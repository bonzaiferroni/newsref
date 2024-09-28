package newsref.model.dto

import newsref.model.data.Article
import newsref.model.data.Source

data class SourceInfo(
    val leadUrl: String,
    val source: Source,
    val article: Article? = null,
    val outletName: String? = null,
    val contents: Set<String> = emptySet(),
    val links: List<LinkInfo> = emptyList(),
    val authors: Set<String>? = null,
)

data class LinkInfo(
    val url: String,
    val urlText: String,
    val context: String,
)