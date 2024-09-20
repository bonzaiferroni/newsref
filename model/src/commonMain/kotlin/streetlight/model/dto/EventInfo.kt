package streetlight.model.dto

import kotlinx.serialization.Serializable
import streetlight.model.core.Area
import streetlight.model.core.Event
import streetlight.model.core.Location

@Serializable
data class EventInfo(
    val event: Event = Event(),
    val location: Location = Location(),
    val area: Area? = null,
    val user: UserInfo = UserInfo(),
    val currentRequest: RequestInfo? = null,
    val requests: List<RequestInfo> = emptyList(),
)