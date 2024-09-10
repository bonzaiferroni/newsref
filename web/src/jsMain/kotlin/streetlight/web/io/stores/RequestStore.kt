package streetlight.web.io.stores

import streetlight.model.core.Request
import streetlight.model.dto.RequestInfo
import streetlight.web.io.client.ApiClient
import streetlight.web.io.client.globalApiClient

class RequestStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun create(request: Request): Int? = client.create("/requests", request)
    suspend fun getInfos(eventId: Int): List<RequestInfo> =
        client.get("/request_info/event/$eventId")
}