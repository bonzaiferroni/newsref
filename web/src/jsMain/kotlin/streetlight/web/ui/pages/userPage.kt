package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h3
import io.kvision.html.image
import streetlight.model.dto.isAdmin
import streetlight.web.Layout
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.gap
import streetlight.web.ui.components.labelInfo
import streetlight.web.ui.components.row
import streetlight.web.ui.components.rows
import streetlight.web.ui.components.userContext

fun Container.userPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        rows {
            gap = Layout.halfGap
            h3("Hello, ${userInfo.username}!")
            userInfo.venmo?.let { labelInfo("venmo", it) }
            userInfo.avatarUrl?.let { image(it) }
            row {
                button("Catalog").onClick { context.navigate(Pages.catalog) }
                button("Account").onClick { context.navigate(Pages.account) }
                if (userInfo.isAdmin) {
                    button("Admin").onClick { context.navigate(Pages.admin) }
                }
                button("Logout", style = ButtonStyle.WARNING).onClick {
                    context.model.logout()
                    context.navigate(Pages.login)
                }
            }
        }
    }
    return null
}