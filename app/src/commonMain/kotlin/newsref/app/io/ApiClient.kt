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
import newsref.model.GetByIdEndpoint
import newsref.model.GetEndpoint
import newsref.model.PostEndpoint
import newsref.model.data.LoginRequest

class ApiClient(
    val baseUrl: String,
    val client: HttpClient = globalKtorClient
) {
    companion object {
        var jwt: String? = null
        var loginRequest: LoginRequest? = null
    }

    suspend inline fun <reified Returned> get(
        endpoint: GetByIdEndpoint<Returned>,
        id: Any,
        vararg params: Pair<String, String>?
    ): Returned = request(
        method = HttpMethod.Get,
        path = endpoint.replaceClientId(id),
        body = null,
        params = params
    )

    suspend inline fun <reified Returned> getOrNull(
        endpoint: GetByIdEndpoint<Returned>,
        id: Any,
        vararg params: Pair<String, String>?
    ): Returned? = requestOrNull(
        method = HttpMethod.Get,
        path = endpoint.replaceClientId(id),
        body = null,
        params = params
    )

    suspend inline fun <reified Returned> getSameData(
        endpoint: GetByIdEndpoint<*>,
        id: Any,
        vararg params: Pair<String, String>?
    ): Returned = request(
        method = HttpMethod.Get,
        path = endpoint.replaceClientId(id),
        body = null,
        params = params
    )

    suspend inline fun <reified Returned> get(
        endpoint: GetEndpoint<Returned>,
        vararg params: Pair<String, String>?
    ): Returned = request(HttpMethod.Get, endpoint.path, null, *params)

    suspend inline fun <reified Returned> getSameData(
        endpoint: GetEndpoint<*>,
        vararg params: Pair<String, String>?
    ): Returned = request(HttpMethod.Get, endpoint.path, null, *params)

    suspend inline fun <reified Sent, reified Returned> post(
        endpoint: PostEndpoint<Sent, Returned>,
        value: Sent,
        vararg params: Pair<String, String>?
    ): Returned = request(HttpMethod.Post, endpoint.path, value, *params)

    suspend inline fun <reified Sent, reified Returned> postSameData(
        endpoint: PostEndpoint<Sent, *>,
        value: Sent,
        vararg params: Pair<String, String>?
    ): Returned = request(HttpMethod.Post, endpoint.path, value, *params)

    suspend inline fun <reified Sent, reified Received> request(
        method: HttpMethod,
        path: String,
        body: Sent,
        vararg params: Pair<String, String>?
    ): Received = requestOrNull(method, path, body, *params) ?: error("Request result is null")

    suspend inline fun <reified Sent, reified Received> requestOrNull(
        method: HttpMethod,
        path: String,
        body: Sent,
        vararg params: Pair<String, String>?
    ): Received? {
        val response = sendRequest<Sent>(method, "$baseUrl$path", body, *params)
        if (response.status == HttpStatusCode.OK) return response.body()
        if (response.status == HttpStatusCode.Unauthorized && loginRequest != null) {
            val auth = login()
            if (auth != null) {
                return sendRequest<Sent>(method, path, body, *params).body()
            }
        }
        return null
    }

    suspend inline fun <reified Sent> sendRequest(
        method: HttpMethod,
        url: String,
        body: Sent,
        vararg params: Pair<String, String>?
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
                if (it == null) return@forEach
                parameters.append(it.first, it.second)
            }
        }
        if (body != null && body !is Unit) {
            setBody(body)
        }
    }

    suspend fun login(request: LoginRequest? = null): Auth? {
        request?.let { loginRequest = it }
        val response = client.post("$baseUrl${Api.Login.path}") {
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
}

val globalApiClient = ApiClient("http://192.168.1.100:8080")