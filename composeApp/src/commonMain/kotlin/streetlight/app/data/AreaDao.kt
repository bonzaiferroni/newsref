package streetlight.app.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import streetlight.model.Area

class AreaDao : ApiDao() {
    suspend fun addArea(area: Area): Int {
        val response = web.post("$address/areas") {
            contentType(ContentType.Application.Json)
            setBody(area)
        }
        // return id from response
        return if (response.status == HttpStatusCode.Created) {
            response.body()
        } else {
            -1
        }
    }

    suspend fun getAll(): List<Area> {
        return web.get("$address/areas").body()
    }
}