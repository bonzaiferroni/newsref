package streetlight.web.io.stores

import streetlight.model.Api
import streetlight.model.core.Location
import streetlight.web.io.client.ApiClient
import streetlight.web.io.client.globalApiClient

class LocationStore(
    val client: ApiClient = globalApiClient
) {
    suspend fun get(id: Int): Location = client.get(Api.location, id)
    suspend fun getAll(): List<Location> = client.get(Api.location)
    suspend fun create(location: Location): Location = client.create(Api.location, location)
    suspend fun delete(location: Location): Boolean = client.deleteData(Api.location, location)
    suspend fun update(location: Location): Location = client.update(Api.location, location)
}