package streetlight.web.io

import io.kvision.html.P
import io.kvision.rest.HttpMethod
import streetlight.model.Area
import kotlin.js.Promise

class AreaStore(
    private val client: StoreClient = StoreClient(),
) {
    suspend fun create(area: Area): Int? = client.authRequest(HttpMethod.POST, "/areas", area)
    fun getAll(): Promise<List<Area>> = client.get("/areas")
    fun get(id: Int): Promise<Area?> = client.get("/areas/$id")
    fun update(area: Area): Promise<Boolean> = client.put("/areas", area)
    fun delete(id: Int): Promise<Boolean> = client.delete("/areas", id)
}