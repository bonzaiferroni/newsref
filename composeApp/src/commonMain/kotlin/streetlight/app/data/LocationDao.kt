package streetlight.app.data

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import streetlight.model.Location

class LocationDao : ApiDao() {
    suspend fun addLocation(location: Location): String {
        val response = web.post("$address/locations") {
            contentType(ContentType.Application.Json)
            setBody(location)
        }
        return response.status.toString()
    }
}