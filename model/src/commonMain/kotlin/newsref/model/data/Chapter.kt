package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: Long = 0,
    val title: String? = null,
    val score: Int,
    val size: Int,
    val cohesion: Float,
    val averageAt: Instant,
)