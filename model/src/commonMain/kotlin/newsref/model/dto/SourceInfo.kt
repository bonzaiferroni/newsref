package newsref.model.dto

import com.eygraber.uri.Url
import newsref.model.data.Article
import newsref.model.data.Source

data class SourceInfo(
    val id: Long = 0,
    val leadUrl: Url,
    val source: Source,
    val document: DocumentInfo? = null,
)

data class DocumentInfo(
    val article: Article,
    val contents: Set<String> = emptySet(),
    val links: List<LinkInfo> = emptyList(),
    val authors: Set<String>? = null,
)

data class LinkInfo(
    val url: Url,
    val anchorText: String,
    val context: String,
)