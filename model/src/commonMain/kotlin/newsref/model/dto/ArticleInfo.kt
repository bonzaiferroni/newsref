package newsref.model.dto

import newsref.model.data.Document
import newsref.model.data.Source
import newsref.model.data.Link

data class ArticleInfo(
    val source: Source,
    val document: Document? = null,
    val outletName: String? = null,
    val links: List<Link> = emptyList()
)