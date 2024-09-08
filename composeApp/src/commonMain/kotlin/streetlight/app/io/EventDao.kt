package streetlight.app.io

import streetlight.model.dto.EventInfo
import streetlight.model.dto.RequestInfo
import streetlight.model.core.Event
import streetlight.model.dto.ImageUploadRequest

class EventDao(
    private val client: ApiClient,
) {
    suspend fun create(event: Event): Int = client.create("/events", event)
    suspend fun getAll(): List<Event> = client.getBody("/events")
    suspend fun get(id: Int): Event? = client.getBody("/events/$id")
    suspend fun search(search: String, count: Int = 10): List<Event> =
        client.getBody("/events?search=$search&count=$count")
    suspend fun update(event: Event): Boolean = client.update("/events", event.id, event)
    suspend fun delete(id: Int): Boolean = client.delete("/events", id)
    suspend fun getInfo(id: Int): EventInfo? = client.getBody("/event_info/$id")
    suspend fun getAllInfo(): List<EventInfo> = client.getBody("/event_info")
    suspend fun getAllCurrentInfos(): List<EventInfo> = client.getBody("/event_info/current")
    suspend fun getProfile(id: Int): List<RequestInfo> = client.getBody("/event_profile/$id")
    suspend fun postImage(request: ImageUploadRequest): Boolean = client.post("/events/upload", request)
}