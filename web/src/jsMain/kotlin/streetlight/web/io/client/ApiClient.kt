package streetlight.web.io.client

import io.kvision.rest.*
import kotlinx.coroutines.await
import streetlight.model.dto.AuthInfo
import streetlight.model.dto.LoginRequest
import streetlight.web.HTTP_OK
import streetlight.web.io.stores.LocalStore

val globalApiClient = ApiClient()

class ApiClient(
    private val localStore: LocalStore = LocalStore()
) : Requester by RequesterComponent(),
    Authorizer by AuthorizerComponent() {

    init {
        jwt = localStore.jwt
    }

    var loginRequest = LoginRequest(username = localStore.username ?: "", session = localStore.session)

    suspend fun coldLogin(loginRequest: LoginRequest): Boolean {
        return try {
            this.loginRequest = loginRequest
            login()
        } catch (e: Unauthorized) {
            false
        }
    }

    suspend fun login(): Boolean {
        val response: RestResponse<AuthInfo> =
            request<AuthInfo, LoginRequest>(HttpMethod.POST, "/login", loginRequest).await()
        if (response.response.status == HTTP_OK) {
            console.log("StoreClient.login: received OK from login")
            response.data.session?.let {
                loginRequest = loginRequest.copy(session = it)
                if (localStore.save == true) {
                    localStore.session = loginRequest.session
                    localStore.username = loginRequest.username
                }
            }
            loginRequest = loginRequest.copy(password = null)
            if (localStore.save == true) {
                localStore.jwt = response.data.jwt
            }
            jwt = response.data.jwt
            return true
        } else {
            console.log("StoreClient.login: failed: ${response.response.statusText}")
            return false
        }
    }

    suspend inline fun <reified T : Any> get(endpoint: String): T = authRequest(::login) {
        request<T>(HttpMethod.GET, endpoint)
    }.data

    suspend inline fun <reified Received : Any, reified Sent : Any> post(
        endpoint: String,
        data: Sent
    ): Received = authRequest(::login) {
        request<Received, Sent>(HttpMethod.POST, endpoint, data)
    }.data

    suspend inline fun <reified Sent : Any> post(endpoint: String): Boolean = authRequest(::login) {
        request<Sent>(HttpMethod.POST, endpoint)
    }.response.status == HTTP_OK

    suspend inline fun <reified Received : Any, reified Sent : Any> put(
        endpoint: String, data: Sent
    ): Received = authRequest(::login) {
        request<Received, Sent>(HttpMethod.PUT, endpoint, data)
    }.data

    suspend inline fun <reified Sent : Any> putRespondBool(endpoint: String, data: Sent): Boolean = authRequest(::login) {
        request<Boolean, Sent>(HttpMethod.PUT, endpoint, data)
    }.data

    suspend fun delete(endpoint: String, id: Int): dynamic = authRequestDynamic(::login) {
        console.log("deleting")
        requestDynamic(HttpMethod.DELETE, "$endpoint$id")
    }

    suspend inline fun <reified Received : Any, reified Sent : Any> create(
        endpoint: String,
        data: Sent
    ): Received = authRequest(::login) {
        request<Received, Sent>(HttpMethod.POST, endpoint, data)
    }.data

    suspend inline fun <reified T : Any> update(endpoint: String, id: Int, data: T): Boolean =
        authRequestDynamic(::login) { requestDynamic(HttpMethod.PUT, "$endpoint/$id", data) }
            .response.status == HTTP_OK

    fun logout() {
        jwt = null
        localStore.jwt = null
        localStore.session = null
        loginRequest = loginRequest.copy(session = null)
    }
}