package streetlight.app.io

import streetlight.model.Location

class LocationDao(
    private val client: ApiClient,
) {
    suspend fun search(search: String, count: Int = 10): List<Location> =
        client.getBody("/locations?search=$search&count=$count")
    suspend fun addLocation(location: Location): Int = client.create("/locations", location)
    suspend fun getAll(): List<Location> = client.getBody("/locations")
}