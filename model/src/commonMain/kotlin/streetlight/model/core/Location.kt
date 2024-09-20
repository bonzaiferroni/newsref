package streetlight.model.core

import kotlinx.serialization.Serializable
import streetlight.model.enums.LocationType

@Serializable
data class Location(
    override val id: Int = 0,
    val userId: Int = 0,
    val areaId: Int = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val type: LocationType = LocationType.Unknown,
    val notes: String = "",
) : IdModel