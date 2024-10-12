package newsref.model.dto

import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.data.Article
import newsref.model.data.Source

data class FetchInfo(
    val id: Long = 0,
    val source: Source,
    val page: PageInfo? = null,
)

data class PageInfo(
    val article: Article,
    val leadUrl: CheckedUrl,
    val pageUrl: CheckedUrl,
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