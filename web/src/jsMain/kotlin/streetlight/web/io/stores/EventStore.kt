package streetlight.web.io.stores

import streetlight.model.Event
import streetlight.model.dto.EventInfo
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient

class EventStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun create(event: Event): Int? = client.create("/events", event)
    suspend fun getInfo(id: Int): EventInfo = client.get("/event_info/$id")
    suspend fun getInfos(): List<EventInfo> = client.get("/event_info")
}