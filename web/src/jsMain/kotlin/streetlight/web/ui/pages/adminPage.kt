package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.p
import streetlight.model.dto.isAdmin
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.ui.components.userContext

fun Container.adminPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        if (userInfo.isAdmin) {
            p("Hello admin!")
        } else {
            p("You are not an admin.")
        }
    }
    return null
}