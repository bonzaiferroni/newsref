package streetlight.web.io.stores

import streetlight.model.Event
import streetlight.model.dto.EventInfo
import streetlight.web.io.StoreClient
import streetlight.web.io.globalStoreClient

class EventStore(
    private val client: StoreClient = globalStoreClient,
) {
    suspend fun create(event: Event): Int? = client.create("/events", event)
    suspend fun getInfo(id: Int): EventInfo = client.get("/event_info/$id")
    suspend fun getInfos(): List<EventInfo> = client.get("/event_info")
}