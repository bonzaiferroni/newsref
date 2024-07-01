package streetlight.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventInfo(
    val id: Int = 0,
    @SerialName("location_name")
    val locationName: String = "",
    @SerialName("location_id")
    val locationId: Int = 0,
    @SerialName("time_start")
    val timeStart: Long = 0L,
    val hours: Float = 0f,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @SerialName("area_id")
    val areaId: Int = 0,
    @SerialName("area_name")
    val areaName: String = "",
)