package newsref.app.io

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import newsref.app.model.Auth
import newsref.model.Api
import newsref.model.Endpoint
import newsref.model.data.LoginRequest

class ApiClient(
    val baseUrl: String,
    val client: HttpClient = globalKtorClient
) {
    companion object {
        var jwt: String? = null
        var loginRequest: LoginRequest? = null
    }

    suspend inline fun <reified Received> getById(
        id: Int,
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received = getById(id.toLong(), endpoint, *params)

    suspend inline fun <reified Received> getById(
        id: Long,
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received = request(
        method = HttpMethod.Get,
        url = "$baseUrl${endpoint.clientIdTemplate.replace(":id", id.toString())}",
        body = null,
        params = params
    )

    suspend inline fun <reified Received> get(
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received = request(HttpMethod.Get, "$baseUrl${endpoint.path}", null, *params)

    suspend inline fun <reified Received, reified Sent> post(
        endpoint: Endpoint,
        value: Sent,
        vararg params: Pair<String, String>
    ): Received = request(HttpMethod.Post, "$baseUrl${endpoint.path}", value, *params)

    suspend inline fun <reified Received, reified Sent> request(
        method: HttpMethod,
        url: String,
        body: Sent,
        vararg params: Pair<String, String>
    ): Received = requestOrNull(method, url, body, *params) ?: error("Request result is null")

    suspend inline fun <reified Received, reified Sent> requestOrNull(
        method: HttpMethod,
        url: String,
        body: Sent,
        vararg params: Pair<String, String>
    ): Received? {
        val response = sendRequest<Sent>(method, url, body, *params)
        if (response.status == HttpStatusCode.OK) return response.body()
        if (response.status == HttpStatusCode.Unauthorized && loginRequest != null) {
            val auth = login()
            if (auth != null) {
                return sendRequest<Sent>(method, url, body, *params).body()
            }
        }
        return null
    }

    suspend fun login(request: LoginRequest? = null): Auth? {
        request?.let { loginRequest = it }
        val response = client.post("$baseUrl${Api.loginEndpoint.path}") {
            setBody(loginRequest)
        }
        if (response.status != HttpStatusCode.OK) {
            return null
        }
        val auth: Auth = response.body()
        jwt = auth.jwt
        loginRequest = loginRequest?.copy(
            password = null,
            refreshToken = auth.refreshToken
        )
        return auth
    }

    suspend inline fun <reified Sent> sendRequest(
        method: HttpMethod,
        url: String,
        body: Sent,
        vararg params: Pair<String, String>
    ) = client.request(
        urlString = url,
    ) {
        contentType(ContentType.Application.Json)
        this.method = method
        jwt?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }
        url {
            params.forEach {
                parameters.append(it.first, it.second)
            }
        }
        if (body != null && body !is Unit) {
            setBody(body)
        }
    }
}

val globalApiClient = ApiClient("http://192.168.1.100:8080")