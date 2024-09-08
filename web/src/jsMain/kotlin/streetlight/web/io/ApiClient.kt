package streetlight.web.io

import io.kvision.rest.*
import kotlinx.coroutines.await
import streetlight.model.dto.AuthInfo
import streetlight.model.dto.LoginInfo
import streetlight.web.HTTP_OK
import streetlight.web.baseAddress
import streetlight.web.io.stores.LocalStore
import kotlin.js.Promise

val globalApiClient = ApiClient()

class ApiClient() {
    val restClient = RestClient()
    val localStore = LocalStore()

    var jwt = localStore.jwt

    var loginInfo = LoginInfo(username = localStore.username ?: "", session = localStore.session)

    val apiAddress = "$baseAddress/api/v1"

    val tokenHeaders = {
        listOf(Pair("Authorization", "Bearer $jwt") )
    }

    inline fun <reified Returned: Any, reified Sent : Any> requestData(
        method: HttpMethod,
        endpoint: String,
        data: Sent?,
    ): Promise<RestResponse<Returned>> {
        return restClient.request("$apiAddress$endpoint") {
            this.method = method
            this.data = data
            this.headers = tokenHeaders
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
            this.headers = tokenHeaders
            this.responseBodyType = ResponseBodyType.TEXT
            this.serializer = getSerializer<Sent>()
        }
    }

    suspend fun login(): Boolean {
        try {
            val response: RestResponse<AuthInfo> = restClient.request<AuthInfo>("$apiAddress/login") {
                this.method = HttpMethod.POST
                this.data = loginInfo
                this.serializer = LoginInfo.serializer()
            }.await()
            if (response.response.status == HTTP_OK) {
                console.log("StoreClient.login success")
                response.data.session?.let {
                    loginInfo = loginInfo.copy(session = it)
                    if (localStore.save == true) {
                        localStore.session = loginInfo.session
                        localStore.username = loginInfo.username
                    }
                }
                loginInfo = loginInfo.copy(password = null)
                if (localStore.save == true) {
                    localStore.jwt = response.data.jwt
                }
                jwt = response.data.jwt
                return true
            } else {
                console.log("StoreClient.login: ${response.response.statusText}")
                return false
            }
        } catch (e: Exception) {
            console.log("StoreClient.login: exception: $e")
            return false
        }
    }

    suspend inline fun <reified T: Any> authRequest(
        requester: () -> Promise<T>
    ): T? {
        console.log("StoreClient.authRequest: requesting")
        try {
            return requester().await()
        } catch (e: Unauthorized) {
            console.log("StoreClient.authRequest: unauthorized, attempt auth")
            val authorized = login()
            if (authorized) {
                console.log("StoreClient.authRequest: authorized")
                return requester().await()
            }
        }
        return null
    }

    suspend inline fun <reified T: Any> get(endpoint: String): T {
        return restClient.call<T>("$apiAddress$endpoint").await()
    }

    suspend inline fun <reified T: Any> getAuth(endpoint: String): T? = authRequest {
        restClient.call<T>("$apiAddress$endpoint") {
            this.headers = tokenHeaders
        }
    }

    suspend inline fun <reified Received: Any, reified Sent: Any> post(
        endpoint: String, data: Sent
    ): Received? {
        return authRequest {
            restClient.call<Received>("$apiAddress$endpoint") {
                this.method = HttpMethod.POST
                this.data = data
                this.headers = tokenHeaders
                this.serializer = getSerializer<Sent>()
            }
        }
    }

    suspend fun delete(endpoint: String, id: Int): Boolean? {
        return authRequest {
            return restClient.requestOnly("$apiAddress$endpoint$id") {
                this.method = HttpMethod.DELETE
                this.headers = tokenHeaders
            }
        }
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
    fun logout() {
        jwt = null
        localStore.jwt = null
        localStore.session = null
        loginInfo = loginInfo.copy(session = null)
    }
}

suspend inline fun RestClient.requestOnly(
    url: String,
    crossinline block:  RestRequestConfig<String, dynamic>.() -> Unit = {}
): Boolean {
    try {
        val result = this.request<String>(url, block).await()
        return result.response.status == HTTP_OK
    } catch (e: Exception) {
        when (e) {
            is XHRError -> return true
        }
        console.log("StoreClient.requestOnly: exception: $e")
        throw e
    }
}