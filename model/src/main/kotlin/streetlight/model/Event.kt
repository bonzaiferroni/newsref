package streetlight.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Int = 0,
    val locationId: Int = 0,
    val userId: Int = 0,
    val timeStart: Long = 0L,
    val hours: Float = 0f,
    val url: String? = null,
)