package streetlight.web.io.stores

import streetlight.model.Api
import streetlight.model.core.Event
import streetlight.model.dto.EventInfo
import streetlight.web.io.client.ApiClient
import streetlight.web.io.client.globalApiClient

class EventStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun create(event: Event): Int = client.create(Api.event, event)
    suspend fun getInfo(id: Int): EventInfo = client.get(Api.eventInfo, id)
    suspend fun getInfos(): List<EventInfo> = client.get(Api.eventInfo)
}