package streetlight.app.data

import streetlight.model.Event

class EventDao(
    private val client: ApiClient,
) {
    suspend fun create(event: Event): Int = client.create("/events", event)
    suspend fun getAll(): List<Event> = client.getBody("/events")
    suspend fun get(id: Int): Event? = client.getBody("/events/$id")
    suspend fun search(search: String, count: Int = 10): List<Event> =
        client.getBody("/events?search=$search&count=$count")
    suspend fun update(id: Int, event: Event): Boolean = client.update("/events", id, event)
    suspend fun delete(id: Int): Boolean = client.delete("/events", id)
}