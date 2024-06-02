package streetlight.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtEvent(
    val id: Int = 0,
    @SerialName("user_id")
    val userId: Int = 0,
    @SerialName("event_id")
    val eventId: Int = 0,
    @SerialName("stream_url")
    val streamUrl: String? = null,
    val tips: Double? = null,
)