package streetlight.app.io

import streetlight.model.Area
import streetlight.model.Event
import streetlight.model.Location

class LocationDao(
    private val client: ApiClient,
) {
    suspend fun search(search: String, count: Int = 10): List<Location> =
        client.getBody("/locations?search=$search&count=$count")
    suspend fun create(location: Location): Int = client.create("/locations", location)
    suspend fun getAll(): List<Location> = client.getBody("/locations")
    suspend fun update(location: Location): Boolean = client.update("/events", location.id, location)
    suspend fun get(id: Int): Location? = client.getBody("/locations/$id")
}