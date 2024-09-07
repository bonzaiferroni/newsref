package streetlight.web.io.stores

import streetlight.model.Area
import streetlight.web.io.StoreClient
import streetlight.web.io.globalStoreClient

class AreaStore(
    private val client: StoreClient = globalStoreClient,
) {
    suspend fun create(area: Area): Int? = client.create("/areas", area)
    suspend fun getAll(): List<Area> = client.get("/areas")
    suspend fun get(id: Int): Area = client.get("/areas/$id")
    suspend fun update(area: Area): Boolean = client.update("/areas", area.id, area)
    suspend fun delete(id: Int): Boolean = client.delete("/areas/", id)
}