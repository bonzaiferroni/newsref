package newsref.model.dto

import com.eygraber.uri.Url
import newsref.model.data.Article
import newsref.model.data.Source
import newsref.model.data.SourceType

data class SourceInfo(
    val id: Long = 0,
    val leadUrl: Url,
    val source: Source,
    val document: DocumentInfo? = null,
)

data class DocumentInfo(
    val article: Article,
    val docUrl: Url?,
    val type: SourceType,
    val outletId: Int,
    val contents: Set<String> = emptySet(),
    val links: List<LinkInfo> = emptyList(),
    val authors: Set<String>? = null,
)

data class LinkInfo(
    val url: Url,
    val anchorText: String,
    val context: String,
)