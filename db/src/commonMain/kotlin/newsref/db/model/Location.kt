package newsref.db.model

import kabinet.model.GeoPoint

data class Location(
    val id: Int,
    val name: String,
    val geoPoint: GeoPoint,
    val northEast: GeoPoint,
    val southWest: GeoPoint,
)