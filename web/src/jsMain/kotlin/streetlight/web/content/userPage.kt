package streetlight.web.content

import io.kvision.core.Container
import io.kvision.html.p
import io.kvision.routing.Routing
import streetlight.web.io.stores.AppModel
import streetlight.web.io.userContext

fun Container.userPage(appModel: AppModel,  routing: Routing) {
    userContext(appModel, routing) { userInfo ->
        p("Hello, ${userInfo.username}!")
    }
}