package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h3
import io.kvision.html.image
import newsref.model.dto.isAdmin
import newsref.web.Layout
import newsref.web.core.AppContext
import newsref.web.core.Pages
import newsref.web.core.PortalEvents
import newsref.web.core.navigate
import newsref.web.gap
import newsref.web.ui.components.col
import newsref.web.ui.components.labelInfo
import newsref.web.ui.components.row
import newsref.web.ui.components.userContext

fun Container.userPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        col {
            gap = Layout.halfGap
            h3("Hello, ${userInfo.username}!")
            userInfo.venmo?.let { labelInfo("venmo", it) }
            userInfo.avatarUrl?.let { image(it) }
            row {
                button("Catalog").onClick { context.navigate(Pages.catalog) }
                button("Atlas").onClick { context.navigate(Pages.atlas) }
            }
            row {
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