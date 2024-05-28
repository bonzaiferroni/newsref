package streetlight.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import streetlight.model.User

class ApiClient {
    val web = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
    }
    val address = "http://localhost:8080"

    private var token = ""

    suspend inline fun <reified T> create(endpoint: String, data: T): Int {
        val response = post("$address$endpoint", data)
        return if (response.status == HttpStatusCode.Created) {
            response.body()
        } else {
            -1
        }
    }

    suspend inline fun <reified T> post(endpoint: String, data: T): HttpResponse {
        return web.post("$address$endpoint") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }
    }

    suspend inline fun <reified T> getBody(endpoint: String): T {
        val response = web.get("$address$endpoint")
        return response.body()
    }

    suspend fun get(endpoint: String): HttpResponse {
        return web.get("$address$endpoint")
    }

    suspend fun login(username: String, password: String): HttpResponse {
        val response = post("/login", User(name = username, password = password))
        if (response.status == HttpStatusCode.OK) {
            token = response.body<TokenBox>().token
        }
        return response
    }
}

@Serializable
data class TokenBox(
    val token: String,
)