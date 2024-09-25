package newsref.model.dto

import newsref.model.data.Source
import newsref.model.data.Link

data class ArticleInfo(
    val source: Source,
    val links: List<Link>
)