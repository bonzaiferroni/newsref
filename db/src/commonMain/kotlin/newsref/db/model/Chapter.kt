package newsref.db.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: Long = 0,
    val storyId: Long? = null,
    val parentId: Long? = null,
    val locationId: Int? = null,
    val title: String? = null,
    val summary: String? = null,
    val score: Int,
    val size: Int,
    val cohesion: Float,
    val storyDistance: Float?,
    val level: Int,
    val createdAt: Instant,
    val averageAt: Instant,
    // val earliestAt: Instant,
)