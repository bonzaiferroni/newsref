package newsref.web.core

import io.kvision.routing.Routing
import newsref.web.io.stores.AppModel

data class AppContext(
    val model: AppModel,
    val routing: Routing,
)