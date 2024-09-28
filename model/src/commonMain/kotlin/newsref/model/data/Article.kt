package newsref.model.data

import kotlinx.datetime.Instant

data class Article(
    val id: Long = 0,
    val sourceId: Long = 0,
    val headline: String = "",
    val alternativeHeadline: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val section: String? = null,
    val keywords: String? = null,
    val wordCount: Int? = null,
    val accessedAt: Instant = Instant.DISTANT_PAST,
    val publishedAt: Instant? = null,
    val modifiedAt: Instant? = null,
)