package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.Url

data class Article(
    val id: Long = 0,
    val sourceId: Long = 0,
    val headline: String = "",
    val alternativeHeadline: String? = null,
    val description: String? = null,
    val imageUrl: Url? = null,
    val section: String? = null,
    val keywords: List<String>? = null,
    val wordCount: Int? = null,
    val isFree: Boolean? = null,
    val thumbnail: String? = null,
    val language: String? = null,
    val commentCount: Int? = null,
    val accessedAt: Instant = Instant.DISTANT_PAST,
    val publishedAt: Instant? = null,
    val modifiedAt: Instant? = null,
)