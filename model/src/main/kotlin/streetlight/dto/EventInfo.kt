package streetlight.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventInfo(
    val id: Int,
    val locationName: String,
    val locationId: Int,
    val timeStart: Long,
    val hours: Float,
    val latitude: Double,
    val longitude: Double,
    val areaId: Int,
    val areaName: String,
)