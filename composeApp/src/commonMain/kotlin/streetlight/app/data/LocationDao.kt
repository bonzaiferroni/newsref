package streetlight.app.data

import streetlight.model.Location

class LocationDao(
    private val web: ApiClient,
) {
    suspend fun addLocation(location: Location): Int {
        return web.create("/locations", location)
    }

    suspend fun getAll(): List<Location> {
        return web.getBody("/locations")
    }
}