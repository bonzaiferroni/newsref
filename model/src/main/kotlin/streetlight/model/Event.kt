package streetlight.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Int = 0,
    val locationId: Int = 0,
    val userId: Int = 0,
    val timeStart: Long = 0L,
    val hours: Float = 0f,
    val url: String? = null,
    val imageUrl: String? = null,
    val streamUrl: String? = null,
    val name: String? = null,
    val description: String? = null,
    val status: EventStatus = EventStatus.Pending,
    val currentSongId: Int? = null,
    val cashTips: Float? = null,
    val cardTips: Float? = null,
)