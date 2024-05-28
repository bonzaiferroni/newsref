package streetlight.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import streetlight.model.Area

class WebClient {
    val web = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
    }
    val address = "http://localhost:8080"

    private var token = ""

    suspend inline fun <reified T> create(endpoint: String, area: T): Int {
        val response = web.post("$address$endpoint") {
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

    suspend inline fun <reified T> get(endpoint: String): T {
        val response = web.get("$address$endpoint")
        return response.body()
    }
}