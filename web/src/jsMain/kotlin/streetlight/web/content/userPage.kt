package streetlight.web.content

import io.kvision.core.Container
import io.kvision.html.p
import io.kvision.routing.Routing
import streetlight.web.core.PortalEvents
import streetlight.web.io.stores.AppModel
import streetlight.web.io.userContext

fun Container.userPage(appModel: AppModel,  routing: Routing): PortalEvents? {
    userContext(appModel, routing) { userInfo ->
        p("Hello, ${userInfo.username}!")
    }
    return null
}