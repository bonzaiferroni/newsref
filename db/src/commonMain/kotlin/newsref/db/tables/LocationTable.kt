package newsref.db.tables

import klutch.db.point
import klutch.utils.toGeoPoint
import newsref.db.model.Location
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.ResultRow

internal object LocationTable : IntIdTable("location") {
    val name = text("name").uniqueIndex()
    val geoPoint = point("geo_point")
    val northEast = point("north_east")
    val southWest = point("south_west")
}

internal fun ResultRow.toLocation() = Location(
    id = this[LocationTable.id].value,
    name = this[LocationTable.name],
    geoPoint = this[LocationTable.geoPoint].toGeoPoint(),
    northEast = this[LocationTable.northEast].toGeoPoint(),
    southWest = this[LocationTable.southWest].toGeoPoint(),
)