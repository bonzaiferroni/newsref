package streetlight.web.io

import io.kvision.rest.*
import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.w3c.fetch.ResponseType
import streetlight.model.User
import streetlight.model.dto.AuthInfo
import streetlight.web.HTTP_OK
import kotlin.js.Promise

class StoreClient {
    val restClient = RestClient()

    var token = ""
    private val username = "admin"
    private val password = "admin"

    private val baseAddress = "http://192.168.1.122:8080"

    // val baseAddress = "https://streetlight.ing"
    val apiAddress
        get() = "$baseAddress/api/v1"

    inline fun <reified T: Any, reified V : Any> request(
        method: HttpMethod,
        endpoint: String,
        data: V?,
    ): Promise<RestResponse<T>> {
        return restClient.request("$apiAddress$endpoint") {
            this.method = method
            contentType = "application/json"
            this.data = data
            this.headers = {
                listOf(Pair("Authorization", "Bearer $token") )
            }
        }
    }

    suspend fun login(): Boolean {
        val response: RestResponse<AuthInfo> = restClient.request<AuthInfo>("$apiAddress/login") {
            this.method = HttpMethod.POST
            contentType = "application/json"
            this.data = User(name = username, password = password)
            this.serializer = User.serializer()
        }.await()
        if (response.response.status == HTTP_OK) {
            token = response.data.token
            return true
        }
        return false
    }

    suspend inline fun <reified T: Any, reified V : Any> authRequest(
        method: HttpMethod,
        endpoint: String,
        data: V?,
    ): T? {
        try {
            val result: RestResponse<T> = request<T, V>(method, endpoint, data).await()
            if (result.response.status == HTTP_OK) {
                return result.data
            }
        } catch (e: Unauthorized) {
            val authorized = login()
            if (authorized) {
                return request<T, V>(method, endpoint, data).await().data
            }
        }
        return null
    }

    inline fun <reified T> get(endpoint: String): Promise<T> {
        return restClient.call("$apiAddress$endpoint")
    }

    inline fun <reified T : Any, reified V : Any> post(endpoint: String, data: V): Promise<T> {
        return restClient.post("$apiAddress$endpoint", data)
    }

    inline fun <reified T : Any, reified V : Any> put(endpoint: String, data: V): Promise<T> {
        return restClient.call("$apiAddress$endpoint") {
            method = HttpMethod.PUT
            contentType = "application/json"
            this.data = data
        }
    }

    inline fun <reified T> delete(endpoint: String, id: Int): Promise<T> {
        return restClient.call("$apiAddress$endpoint/$id") {
            method = HttpMethod.DELETE
        }
    }
}