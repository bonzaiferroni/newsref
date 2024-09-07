package streetlight.web.io

import io.kvision.rest.*
import kotlinx.coroutines.await
import streetlight.model.dto.AuthInfo
import streetlight.model.dto.LoginInfo
import streetlight.web.HTTP_OK
import streetlight.web.baseAddress
import streetlight.web.io.stores.LocalStore
import kotlin.js.Promise

val globalStoreClient = StoreClient()

class StoreClient() {
    val restClient = RestClient()
    val localStore = LocalStore()

    var jwt = localStore.jwt

    var loginInfo = LoginInfo(username = localStore.username ?: "", session = localStore.session)

    val apiAddress
        get() = "$baseAddress/api/v1"

    inline fun <reified Returned: Any, reified Sent : Any> requestData(
        method: HttpMethod,
        endpoint: String,
        data: Sent?,
    ): Promise<RestResponse<Returned>> {
        console.log("jwt: $jwt")
        return restClient.request("$apiAddress$endpoint") {
            this.method = method
            this.data = data
            this.headers = {
                listOf(Pair("Authorization", "Bearer $jwt") )
            }
            this.serializer = getSerializer<Sent>()
        }
    }

    inline fun <reified Sent : Any> requestText(
        method: HttpMethod,
        endpoint: String,
        data: Sent?,
    ): Promise<RestResponse<String>> {
        console.log("jwt: $jwt")
        return restClient.request("$apiAddress$endpoint") {
            this.method = method
            this.data = data
            this.headers = {
                listOf(Pair("Authorization", "Bearer $jwt") )
            }
            this.responseBodyType = ResponseBodyType.TEXT
            this.serializer = getSerializer<Sent>()
        }
    }

    suspend fun login(): Boolean {
        val response: RestResponse<AuthInfo> = restClient.request<AuthInfo>("$apiAddress/login") {
            this.method = HttpMethod.POST
            this.data = loginInfo
            this.serializer = LoginInfo.serializer()
        }.await()
        if (response.response.status == HTTP_OK) {
            console.log("login success: ${response.data.jwt}")
            loginInfo = loginInfo.copy(session = response.data.session)
            if (localStore.save == true) {
                localStore.username = loginInfo.username
                localStore.session = loginInfo.session
                localStore.jwt = response.data.jwt
            }
            jwt = response.data.jwt
            return true
        } else {
            console.log("login failed: ${response.response.statusText}")
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

    suspend fun login(loginInfo: LoginInfo): Boolean {
        this.loginInfo = loginInfo
        return login()
    }
}