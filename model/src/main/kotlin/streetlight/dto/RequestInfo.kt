package streetlight.dto

import kotlinx.serialization.Serializable

@Serializable
data class RequestInfo(
    val id: Int,
    val eventId: Int,
    val locationName: String,
    val performanceId: Int,
    val performanceName: String,
    val time: Long = 0L,
)