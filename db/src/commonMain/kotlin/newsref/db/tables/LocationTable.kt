package newsref.db.tables

import newsref.db.core.*
import org.jetbrains.exposed.dao.id.*

object LocationTable : IntIdTable("location") {
    val name = text("name").uniqueIndex()
    val geoPoint = point("geo_point")
    val northEast = point("north_east")
    val southWest = point("south_west")
}