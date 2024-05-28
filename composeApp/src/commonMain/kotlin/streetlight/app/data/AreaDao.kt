package streetlight.app.data

import streetlight.model.Area

class AreaDao(
    private val client: ApiClient,
) {
    suspend fun createArea(area: Area): Int = client.create("/areas", area)
    suspend fun getAll(): List<Area> = client.getBody("/areas")
}