package streetlight.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventInfo(
    val id: Int = 0,
    val locationName: String = "",
    val locationId: Int = 0,
    val timeStart: Long = 0L,
    val hours: Float = 0f,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val areaId: Int = 0,
    val areaName: String = "",
    val url: String? = null,
)