package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.model.Page
import newsref.db.model.Person
import newsref.db.services.LocationLinkerService
import newsref.db.services.PERSON_UNCLEAR
import newsref.krawly.clients.LocationClient
import newsref.krawly.clients.promptTemplate
import newsref.krawly.clients.toGeoPoint
import kotlin.text.lowercase

private val console = globalConsole.getHandle(LocationLinkerClient::class)

class LocationLinkerClient(
    private val client: LocationClient,
    private val service: LocationLinkerService = LocationLinkerService()
) {
    suspend fun readOrCreateLocation(name: String): Int? {
        var locationId = service.readLocationId(name)
        if (locationId == null) {
            val geometry = client.fetchPlaceGeometry(name)
            if (geometry == null) {
                console.logError("Unable to find place geometry for $name")
                return null
            }
            console.log("created location: $name\n${geometry.location.toGeoPoint()}")
            locationId = service.createLocation(
                name = name,
                point = geometry.location.toGeoPoint(),
                northEast = geometry.viewport.northeast.toGeoPoint(),
                southWest = geometry.viewport.southwest.toGeoPoint(),
            )
        }
        return locationId
    }
}