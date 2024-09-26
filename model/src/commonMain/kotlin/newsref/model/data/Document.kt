package newsref.model.data

import kotlinx.datetime.Instant

data class Document(
    val id: Long = 0,
    val sourceId: Long = 0,
    val title: String = "",
    val content: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val accessedAt: Instant = Instant.DISTANT_PAST,
    val publishedAt: Instant? = null,
    val modifiedAt: Instant? = null,
    // wordcount
)