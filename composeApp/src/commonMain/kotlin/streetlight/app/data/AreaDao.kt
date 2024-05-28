package streetlight.app.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import streetlight.model.Area

class AreaDao(
    private val web: WebClient,
) : ApiDao() {
    suspend fun createArea(area: Area): Int = web.create("/areas", area)
    suspend fun getAll(): List<Area> = web.get("/areas")
}