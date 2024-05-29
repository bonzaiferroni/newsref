package streetlight.server.data.location

import streetlight.model.Location
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.lowerCase
import streetlight.server.data.ApiService
import streetlight.server.data.area.AreaEntity

class LocationService : ApiService() {

    suspend fun create(location: Location): Int = dbQuery {
        val dbArea = AreaEntity.findById(location.areaId) ?: return@dbQuery -1
        LocationEntity.new {
            name = location.name
            latitude = location.latitude
            longitude = location.longitude
            area = dbArea
        }.id.value
    }

    suspend fun read(id: Int): Location? = dbQuery {
        LocationEntity.findById(id)?.toLocation()
    }

    suspend fun readAll(): List<Location> = dbQuery {
        LocationEntity.all().map { it.toLocation() }
    }

    suspend fun update(id: Int, location: Location) = dbQuery {
        LocationEntity.findById(id)?.let {
            it.name = location.name
            it.latitude = location.latitude
            it.longitude = location.longitude
            AreaEntity.findById(location.areaId)?.let { a ->
                it.area = a
            }
        }
    }

    suspend fun delete(id: Int) = dbQuery {
        LocationEntity.findById(id)?.delete()
    }

    suspend fun search(search: String, limit: Int): List<Location> {
        return dbQuery {
            LocationEntity.find(Op.build {
                LocationTable.name.lowerCase() like "${search.lowercase()}%"
            })
                .limit(limit)
                .map { it.toLocation() }
        }
    }
}

fun LocationEntity.toLocation() = Location(
    id.value,
    name,
    latitude,
    longitude,
    area.id.value
)
