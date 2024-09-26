package newsref.model.data

import kotlinx.datetime.Instant

data class Source(
    val id: Long = 0,
    val outletId: Int = 0,
    val url: String = "",
    val title: String? = null,
    val content: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val accessedAt: Instant? = null,
    val publishedAt: Instant? = null,
    val modifiedAt: Instant? = null,
    // wordcount
)