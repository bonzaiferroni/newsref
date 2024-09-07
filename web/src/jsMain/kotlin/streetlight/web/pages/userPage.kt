package streetlight.web.pages

import io.kvision.core.Container
import io.kvision.html.p
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.io.userContext

fun Container.userPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        p("Hello, ${userInfo.username}!")
    }
    return null
}