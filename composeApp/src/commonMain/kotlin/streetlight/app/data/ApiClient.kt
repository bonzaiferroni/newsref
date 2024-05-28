package streetlight.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
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
    private val username = "admin"
    private val password = "admin"

    suspend fun create(endpoint: String, data: Any): Int {
        val response = authPost(endpoint, data)
        return if (response.status == HttpStatusCode.Created) {
            response.body()
        } else {
            -1
        }
    }

    suspend fun post(endpoint: String, data: Any): HttpResponse {
        return web.post("$address$endpoint") {
            contentType(ContentType.Application.Json)
            setBody(data)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    suspend fun authPost(endpoint: String, data: Any): HttpResponse {
        var response = post(endpoint, data)
        if (response.status == HttpStatusCode.Unauthorized) {
            response = login(username, password)
            if (response.status == HttpStatusCode.OK) {
                return post(endpoint, data)
            }
        }
        return response
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