package streetlight.web.io.stores

import streetlight.model.Request
import streetlight.model.dto.RequestInfo
import streetlight.web.io.StoreClient

class RequestStore(
    private val client: StoreClient = StoreClient(),
) {
    suspend fun create(request: Request): Int? = client.create("/requests", request)
    suspend fun getInfos(eventId: Int): List<RequestInfo> =
        client.get("/request_info/event/$eventId")
}