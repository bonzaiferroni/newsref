package newsref.web.io.client

import io.kvision.rest.HttpMethod
import io.kvision.rest.RestResponse
import io.kvision.rest.Unauthorized
import kotlinx.coroutines.await
import newsref.model.Api
import newsref.model.Endpoint
import newsref.model.dto.AuthInfo
import newsref.model.dto.LoginRequest
import newsref.web.HTTP_OK
import newsref.web.io.stores.LocalStore

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
            request<AuthInfo, LoginRequest>(HttpMethod.POST, Api.login.path, loginRequest).await()
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

    suspend inline fun <reified T : Any> get(endpoint: Endpoint): T = authRequest(::login) {
        request<T>(HttpMethod.GET, endpoint.path)
    }.data

    suspend inline fun <reified T : Any> get(endpoint: Endpoint, id: Any): T = authRequest(::login) {
        request<T>(HttpMethod.GET, endpoint.replaceClientId(id))
    }.data

    suspend inline fun <reified Received : Any, reified Sent : Any> post(
        endpoint: Endpoint,
        data: Sent
    ): Received = authRequest(::login) {
        request<Received, Sent>(HttpMethod.POST, endpoint.path, data)
    }.data

    suspend inline fun <reified Sent : Any> post(endpoint: Endpoint): Boolean = authRequest(::login) {
        request<Sent>(HttpMethod.POST, endpoint.path)
    }.response.status == HTTP_OK

    suspend inline fun <reified Received : Any, reified Sent : Any> put(
        endpoint: Endpoint, data: Sent
    ): Received = authRequest(::login) {
        request<Received, Sent>(HttpMethod.PUT, endpoint.path, data)
    }.data

    suspend fun delete(endpoint: Endpoint, id: Int): Boolean = authRequest(::login) {
        request<Boolean>(HttpMethod.DELETE, endpoint.replaceClientId(id))
    }.data

    suspend inline fun <reified Sent: Any> deleteData(endpoint: Endpoint, data: Sent): Boolean = authRequest(::login) {
        requestBoolean(HttpMethod.DELETE, endpoint.path, data)
    }.data

    suspend inline fun <reified Received : Any, reified Sent : Any> create(
        endpoint: Endpoint,
        data: Sent
    ): Received = authRequest(::login) {
        request<Received, Sent>(HttpMethod.POST, endpoint.path, data)
    }.data

    suspend inline fun <reified T : Any> update(endpoint: Endpoint, data: T): T = authRequest(::login) {
        request<T, T>(HttpMethod.PUT, endpoint.path, data)
    }.data

    fun logout() {
        jwt = null
        localStore.jwt = null
        localStore.session = null
        loginRequest = loginRequest.copy(session = null)
    }
}