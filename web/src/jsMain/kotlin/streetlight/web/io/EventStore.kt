package streetlight.web.io

import streetlight.model.Event
import streetlight.model.dto.EventInfo

class EventStore(
    private val client: StoreClient = StoreClient(),
) {
    suspend fun create(event: Event): Int? = client.create("/events", event)
    suspend fun getInfo(id: Int): EventInfo = client.get("/event_info/$id")
}