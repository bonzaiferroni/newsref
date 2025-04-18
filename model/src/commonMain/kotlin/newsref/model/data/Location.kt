package newsref.model.data

import kabinet.model.GeoPoint
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int,
    val name: String,
    val geoPoint: GeoPoint,
    val northEast: GeoPoint,
    val southWest: GeoPoint,
)