package newsref.model.dto

import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.data.Article
import newsref.model.data.Source

data class SourceInfo(
    val id: Long = 0,
    val leadUrl: CheckedUrl,
    val source: Source,
    val document: DocumentInfo? = null,
)

data class DocumentInfo(
    val article: Article,
    val docUrl: CheckedUrl?,
    val type: SourceType,
    val outletId: Int,
    val outletName: String?,
    val contents: Set<String> = emptySet(),
    val links: List<LinkInfo> = emptyList(),
    val authors: Set<String>? = null,
)

data class LinkInfo(
    val url: CheckedUrl,
    val anchorText: String,
    val context: String,
)