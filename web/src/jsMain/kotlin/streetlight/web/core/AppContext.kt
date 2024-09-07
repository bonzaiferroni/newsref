package streetlight.web.core

import io.kvision.routing.Routing
import streetlight.web.io.stores.AppModel

data class AppContext(
    val model: AppModel,
    val routing: Routing,
)