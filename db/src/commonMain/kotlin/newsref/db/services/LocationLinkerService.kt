package newsref.db.services

import klutch.db.DbService
import newsref.db.tables.LocationTable
import kabinet.model.GeoPoint
import klutch.utils.toPGpoint
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase

class LocationLinkerService : DbService() {
    suspend fun readLocationId(name: String) = dbQuery {
        LocationTable.select(LocationTable.id)
            .where { LocationTable.name.lowerCase().eq(name.lowercase()) }
            .firstOrNull()?.let { it[LocationTable.id].value }
    }

    suspend fun createLocation(name: String, point: GeoPoint, northEast: GeoPoint, southWest: GeoPoint) = dbQuery {
        LocationTable.insertAndGetId {
            it[this.name] = name
            it[this.geoPoint] = point.toPGpoint()
            it[this.northEast] = northEast.toPGpoint()
            it[this.southWest] = southWest.toPGpoint()
        }.value
    }
}