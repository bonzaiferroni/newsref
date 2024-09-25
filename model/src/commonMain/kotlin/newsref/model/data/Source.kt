package newsref.model.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Source(
    val id: Long = 0,
    val url: String = "",
    val title: String? = null,
    val content: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val accessedAt: Instant = Instant.DISTANT_PAST,
    val publishedAt: Instant? = null,
    val updatedAt: Instant? = null,
)