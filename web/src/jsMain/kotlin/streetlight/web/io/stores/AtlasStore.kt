package streetlight.web.io.stores

import streetlight.model.Api
import streetlight.model.core.Location
import streetlight.web.io.client.ApiClient
import streetlight.web.io.client.globalApiClient

class AtlasStore(
    val client: ApiClient = globalApiClient
) {
    suspend fun get(id: Int): Location = client.get(Api.atlas, id)
    suspend fun getAll(): List<Location> = client.get(Api.atlas)
    suspend fun create(location: Location): Location = client.create(Api.atlas, location)
    suspend fun update(location: Location): Location = client.update(Api.atlas, location)
    suspend fun delete(location: Location): Boolean = client.delete(Api.atlas, location.id)
}