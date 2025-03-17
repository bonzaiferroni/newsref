package newsref.app.io

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.parameters
import newsref.app.model.Auth
import newsref.model.Endpoint

class ApiClient(
    val baseUrl: String,
    val client: HttpClient = globalKtorClient
) {
    companion object {
        var auth: Auth? = null
    }

    fun setAuth(auth: Auth) {
        Companion.auth = auth
    }

    suspend inline fun <reified Received> getById(
        id: Int,
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received =
        getById(id.toLong(), endpoint, *params)

    suspend inline fun <reified Received> getById(
        id: Long,
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received = getRequest("$baseUrl${endpoint.clientIdTemplate.replace(":id", id.toString())}", *params)

    suspend inline fun <reified Received> get(
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received = getRequest("$baseUrl${endpoint.path}", *params)

    suspend inline fun <reified Received> getRequest(
        url: String,
        vararg params: Pair<String, String>
    ): Received = client.get(url) {
        contentType(ContentType.Application.Json)
        val auth = auth
        auth?.let {
            header(HttpHeaders.Authorization, "Bearer ${auth.jwt}")
        }
        if (params.isNotEmpty()) {
            url { params.forEach { parameters.append(it.first, it.second) } }
        }
    }.body()

    suspend inline fun <reified Received, reified Sent> post(
        endpoint: Endpoint,
        value: Sent,
        vararg params: Pair<String, String>
    ): Received = client.post("$baseUrl${endpoint.path}") {
        contentType(ContentType.Application.Json)
        val auth = auth
        auth?.let {
            header(HttpHeaders.Authorization, "Bearer ${auth.jwt}")
        }
        setBody(value)
        if (params.isNotEmpty()) {
            url { params.forEach { parameters.append(it.first, it.second) } }
        }
    }.body()
}

val globalApiClient = ApiClient("http://192.168.1.100:8080")