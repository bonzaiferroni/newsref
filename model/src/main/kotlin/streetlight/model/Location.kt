package streetlight.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @SerialName("area_id")
    val areaId: Int = 0,
)