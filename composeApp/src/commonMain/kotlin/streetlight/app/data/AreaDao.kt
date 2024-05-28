package streetlight.app.data

import streetlight.model.Area

class AreaDao(
    private val web: WebClient,
) {
    suspend fun createArea(area: Area): Int = web.create("/areas", area)
    suspend fun getAll(): List<Area> = web.getBody("/areas")
}