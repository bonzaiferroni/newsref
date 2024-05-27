package streetlight.app.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import streetlight.model.Area
import streetlight.model.Location

class LocationDao : ApiDao() {
    suspend fun addLocation(location: Location): Int {
        val response = web.post("$address/locations") {
            contentType(ContentType.Application.Json)
            setBody(location)
        }
        // return id from response
        return if (response.status == HttpStatusCode.Created) {
            response.body()
        } else {
            -1
        }
    }

    suspend fun getAll(): List<Location> {
        return web.get("$address/locations").body()
    }
}