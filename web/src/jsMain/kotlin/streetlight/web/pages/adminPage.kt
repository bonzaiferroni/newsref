package streetlight.web.pages

import io.kvision.core.Container
import io.kvision.html.p
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.io.userContext

fun Container.adminPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        if (userInfo.roles.contains("admin")) {
            p("Hello admin!")
        } else {
            p("You are not an admin.")
        }
    }
    return null
}