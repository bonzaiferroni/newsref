package newsref.db.model

import newsref.model.data.GeoPoint

data class Location(
    val id: Int,
    val name: String,
    val geoPoint: GeoPoint,
    val northEast: GeoPoint,
    val southWest: GeoPoint,
)