package streetlight.web.io

import io.kvision.html.P
import io.kvision.rest.HttpMethod
import streetlight.model.Area
import kotlin.js.Promise

class AreaStore(
    private val client: StoreClient = StoreClient(),
) {
    suspend fun create(area: Area): Int? = client.create("/areas", area)
    fun getAll(): Promise<List<Area>> = client.get("/areas")
    fun get(id: Int): Promise<Area?> = client.get("/areas/$id")
    suspend fun update(area: Area): Boolean = client.update("/areas", area.id, area)
    suspend fun delete(id: Int): Boolean = client.delete("/areas/", id)
}