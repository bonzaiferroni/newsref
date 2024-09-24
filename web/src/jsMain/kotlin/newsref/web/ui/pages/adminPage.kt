package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.p
import newsref.model.dto.isAdmin
import newsref.web.core.AppContext
import newsref.web.core.PortalEvents
import newsref.web.ui.components.userContext

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