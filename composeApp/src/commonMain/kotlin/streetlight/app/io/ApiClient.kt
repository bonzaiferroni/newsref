package streetlight.app.io

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import streetlight.model.core.User
import streetlight.model.dto.AuthInfo

class ApiClient {
    val web = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
    }

    companion object{
//        val baseAddress = "http://192.168.1.122:8080"
            val baseAddress = "https://streetlight.ing"
        val apiAddress
            get() = "$baseAddress/api/v1"
    }


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

    suspend fun update(endpoint: String, data: Any): Boolean {
        val response = authRequest(endpoint, HttpMethod.Put, data)
        return response.status == HttpStatusCode.OK
    }

    suspend fun post(endpoint: String, data: Any): Boolean {
        val response = authRequest(endpoint, HttpMethod.Post, data)
        return response.status == HttpStatusCode.OK
    }

    suspend fun request(endpoint: String, requestMethod: HttpMethod, data: Any?): HttpResponse {
        return web.request("$apiAddress$endpoint") {
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
        val response = web.get("$apiAddress$endpoint")
        return response.body()
    }

    suspend fun get(endpoint: String): HttpResponse {
        return web.get("$apiAddress$endpoint")
    }

    suspend fun login(username: String, password: String): HttpResponse {
        val response = request(
            "/login", HttpMethod.Post,
            User(name = username, hashedPassword = password)
        )
        if (response.status == HttpStatusCode.OK) {
            token = response.body<AuthInfo>().jwt ?: throw Exception("not implemented session tokens")
        }
        return response
    }
}