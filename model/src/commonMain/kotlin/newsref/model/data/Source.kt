package newsref.model.data

import kotlinx.datetime.Instant

data class Source(
    val id: Long = 0,
    val url: String = "",
    val title: String? = null,
    val content: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val publishedAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST,
    val accessedAt: Instant = Instant.DISTANT_PAST,
)