package streetlight.app.io

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
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
    val address = "http://streetlight.ing/api/v1"

    private var token = ""
    private val username = "admin"
    private val password = "admin"

    suspend fun create(endpoint: String, data: Any): Int {
        val response = authRequest(endpoint, HttpMethod.Post, data)
        return if (response.status == HttpStatusCode.Created) {
            response.body()
        } else {
            -1
        }
    }

    suspend fun delete(endpoint: String, id: Int): Boolean {
        val response = authRequest("$endpoint/$id", HttpMethod.Delete, null)
        return response.status == HttpStatusCode.OK
    }

    suspend fun update(endpoint: String, id: Int, data: Any): Boolean {
        val response = authRequest("$endpoint/$id", HttpMethod.Put, data)
        return response.status == HttpStatusCode.OK
    }

    suspend fun request(endpoint: String, requestMethod: HttpMethod, data: Any?): HttpResponse {
        return web.request("$address$endpoint") {
            method = requestMethod
            contentType(ContentType.Application.Json)
            setBody(data)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    suspend fun authRequest(endpoint: String, requestMethod: HttpMethod, data: Any?): HttpResponse {
        var response = request(endpoint, requestMethod, data)
        if (response.status == HttpStatusCode.Unauthorized) {
            response = login(username, password)
            if (response.status == HttpStatusCode.OK) {
                return request(endpoint, requestMethod, data)
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
        val response = request(
            "/login", HttpMethod.Post,
            User(name = username, password = password)
        )
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