package streetlight.web.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.html.*
import io.kvision.panel.vPanel
import streetlight.web.Layout
import streetlight.web.components.labelInfo
import streetlight.web.components.row
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.gap
import streetlight.web.io.userContext

fun Container.userPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        vPanel(alignItems = AlignItems.START) {
            gap = Layout.halfGap
            h3("Hello, ${userInfo.username}!")
            userInfo.venmo?.let { labelInfo("venmo", it) }
            userInfo.avatarUrl?.let { image(it) }
            row {
                button("Edit") {
                    onClick {
                        context.routing.navigate("/user/edit")
                    }
                }
                if (userInfo.roles.contains("admin")) {
                    button("Admin") {
                        onClick {
                            context.routing.navigate("/admin")
                        }
                    }
                }
                button("Logout", style = ButtonStyle.WARNING) {
                    onClick {
                        context.model.logout()
                        context.routing.navigate("/login")
                    }
                }
            }
        }
    }
    return null
}