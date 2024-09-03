package streetlight.model.dto

import kotlinx.serialization.Serializable
import streetlight.model.Area
import streetlight.model.Event
import streetlight.model.Location
import streetlight.model.User

@Serializable
data class EventInfo(
    val event: Event = Event(),
    val location: Location = Location(),
    val area: Area = Area(),
    val user: User = User(),
    val currentRequest: RequestInfo? = null,
    val requests: List<RequestInfo> = emptyList(),
)