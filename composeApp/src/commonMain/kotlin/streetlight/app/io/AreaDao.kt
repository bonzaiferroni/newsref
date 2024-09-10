package streetlight.app.io

import streetlight.model.Api
import streetlight.model.core.Area
import streetlight.model.replaceClientId

class AreaDao(
    private val client: ApiClient,
) {
    suspend fun create(area: Area): Int = client.create(Api.area, area)
    suspend fun getAll(): List<Area> = client.getBody(Api.area)
    suspend fun get(id: Int): Area? = client.getBody(Api.area.replaceClientId(id))
    suspend fun update(area: Area): Boolean = client.update(Api.area, area)
    suspend fun delete(id: Int): Boolean = client.delete(Api.area.replaceClientId(id))
}