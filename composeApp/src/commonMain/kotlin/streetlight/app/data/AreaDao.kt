package streetlight.app.data

import streetlight.model.Area

class AreaDao(
    private val client: ApiClient,
) {
    suspend fun create(area: Area): Int = client.create("/areas", area)
    suspend fun getAll(): List<Area> = client.getBody("/areas")
    suspend fun get(id: Int): Area? = client.getBody("/areas/$id")
    suspend fun update(id: Int, area: Area): Boolean = client.update("/areas", id, area)
    suspend fun delete(id: Int): Boolean = client.delete("/areas", id)
}