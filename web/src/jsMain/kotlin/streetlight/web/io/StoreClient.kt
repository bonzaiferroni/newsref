package streetlight.web.io

import io.kvision.rest.*
import kotlinx.coroutines.await
import streetlight.model.User
import streetlight.model.dto.AuthInfo
import streetlight.web.HTTP_OK
import streetlight.web.baseAddress
import kotlin.js.Promise

class StoreClient {
    val restClient = RestClient()

    var token = ""
    private val username = "admin"
    private val password = "admin"

    val apiAddress
        get() = "$baseAddress/api/v1"

    inline fun <reified Returned: Any, reified Sent : Any> requestData(
        method: HttpMethod,
        endpoint: String,
        data: Sent?,
    ): Promise<RestResponse<Returned>> {
        return restClient.request("$apiAddress$endpoint") {
            this.method = method
            this.data = data
            this.headers = {
                listOf(Pair("Authorization", "Bearer $token") )
            }
            this.serializer = getSerializer<Sent>()
        }
    }

    inline fun <reified Sent : Any> requestText(
        method: HttpMethod,
        endpoint: String,
        data: Sent?,
    ): Promise<RestResponse<String>> {
        return restClient.request("$apiAddress$endpoint") {
            this.method = method
            this.data = data
            this.headers = {
                listOf(Pair("Authorization", "Bearer $token") )
            }
            this.responseBodyType = ResponseBodyType.TEXT
            this.serializer = getSerializer<Sent>()
        }
    }

    suspend fun login(): Boolean {
        val response: RestResponse<AuthInfo> = restClient.request<AuthInfo>("$apiAddress/login") {
            this.method = HttpMethod.POST
            this.data = User(name = username, hashedPassword = password)
            this.serializer = User.serializer()
        }.await()
        if (response.response.status == HTTP_OK) {
            // TODO: handle session token
            token = response.data.token ?: "not implemented"
            return true
        }
        return false
    }

    suspend inline fun <reified T: Any> authRequest(
        requester: () -> Promise<T>
    ): T? {
        console.log("requesting")
        try {
            return requester().await()
        } catch (e: Unauthorized) {
            console.log("authorizing")
            val authorized = login()
            if (authorized) {
                return requester().await()
            }
        }
        return null
    }

    suspend inline fun <reified T: Any> get(endpoint: String): T {
        return restClient.call<T>("$apiAddress$endpoint").await()
    }

    suspend fun delete(endpoint: String, id: Int): Boolean {
        val result: RestResponse<String>? = authRequest {
            requestText(HttpMethod.DELETE, "$endpoint$id", null)
        }
        return result?.response?.status == HTTP_OK
    }

    suspend inline fun <reified T: Any, reified V: Any> create(endpoint: String, data: V?): T? {
        val result = authRequest { requestData<T, V>(HttpMethod.POST, endpoint, data) }
        return result?.data
    }

    suspend inline fun <reified T: Any> update(endpoint: String, id: Int, data: T): Boolean {
        val result = authRequest { requestText(HttpMethod.PUT, "$endpoint/$id", data) }
        console.log(result?.response?.status)
        return result?.response?.status == HTTP_OK
    }
}