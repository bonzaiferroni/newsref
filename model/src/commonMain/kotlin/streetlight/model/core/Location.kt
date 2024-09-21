package streetlight.model.core

import kotlinx.serialization.Serializable
import streetlight.model.enums.ResourceType

@Serializable
data class Location(
    override val id: Int = 0,
    val userId: Int? = null,
    val areaId: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val notes: String? = null,
    val geoPoint: GeoPoint = GeoPoint(),
    val types: Set<ResourceType> = emptySet(),
) : IdModel

@Serializable
data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)