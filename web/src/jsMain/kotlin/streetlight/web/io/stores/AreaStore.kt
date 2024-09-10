package streetlight.web.io.stores

import streetlight.model.Api
import streetlight.model.core.Area
import streetlight.web.io.client.ApiClient
import streetlight.web.io.client.globalApiClient

class AreaStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun create(area: Area): Int = client.create(Api.area, area)
    suspend fun getAll(): List<Area> = client.get(Api.area)
    suspend fun get(id: Int): Area = client.get(Api.area, id)
    suspend fun update(area: Area): Boolean = client.update(Api.area, area)
    suspend fun delete(id: Int): Boolean = client.delete(Api.area, id)
}