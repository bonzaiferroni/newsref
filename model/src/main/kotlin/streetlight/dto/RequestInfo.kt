package streetlight.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestInfo(
    val id: Int,
    @SerialName("event_id")
    val eventId: Int,
    @SerialName("location_name")
    val locationName: String,
    @SerialName("performance_id")
    val performanceId: Int,
    @SerialName("performance_name")
    val performanceName: String,
    val artist: String?,
    val notes: String,
    val time: Long = 0L,
    val performed: Boolean = false,
)