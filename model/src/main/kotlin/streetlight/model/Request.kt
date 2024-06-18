package streetlight.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int = 0,
    @SerialName("event_id")
    val eventId: Int = 0,
    @SerialName("performance_id")
    val performanceId: Int = 0,
    val time: Long = 0L,
    val performed: Boolean = false,
)