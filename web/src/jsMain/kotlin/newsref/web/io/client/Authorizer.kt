package newsref.web.io.client

import io.kvision.rest.RestResponse
import io.kvision.rest.Unauthorized
import io.kvision.rest.XHRError
import kotlinx.coroutines.await
import newsref.web.HTTP_OK
import kotlin.js.Promise

interface Authorizer {
    suspend fun <Returned> authRequest(
        login: suspend () -> Boolean,
        requester: () -> Promise<RestResponse<Returned>>,
    ): RestResponse<Returned>
    suspend fun authRequestDynamic(
        login: suspend () -> Boolean,
        requester: () -> Promise<RestResponse<dynamic>>,
    ): RestResponse<dynamic>
}

class AuthorizerComponent: Authorizer {
    override suspend fun <Returned> authRequest(
        login: suspend () -> Boolean,
        requester: () -> Promise<RestResponse<Returned>>,
    ): RestResponse<Returned> {
        return try {
            requester().await()
        } catch (e: Unauthorized) {
            val authorized = login()
            if (authorized) {
                requester().await()
            } else {
                error("ApiClient: Login failed, unable to reauthorize")
            }
        }
    }

    override suspend fun authRequestDynamic(
        login: suspend () -> Boolean,
        requester: () -> Promise<RestResponse<dynamic>>,
    ): RestResponse<dynamic> {
        return try {
            authRequest(login, requester)
        } catch (e: XHRError) {
            val response = e.response ?: throw e
            if (response.status == HTTP_OK) {
                return RestResponse(
                    data = true,
                    response = response,
                    textStatus = response.statusText,
                )
            }
            throw e
        }
    }
}