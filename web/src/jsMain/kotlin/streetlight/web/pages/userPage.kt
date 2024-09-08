package streetlight.web.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.html.button
import io.kvision.html.h3
import io.kvision.html.image
import io.kvision.html.p
import io.kvision.panel.vPanel
import streetlight.web.Layout
import streetlight.web.components.labelInfo
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.gap
import streetlight.web.io.userContext

fun Container.userPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        vPanel(alignItems = AlignItems.START) {
            gap = Layout.halfGap
            h3("Hello, ${userInfo.username}!")
            labelInfo("name", userInfo.name)
            labelInfo("email", userInfo.email)
            userInfo.venmo?.let {
                labelInfo("venmo", it)
            }
            userInfo.avatarUrl?.let {
                image(it)
            }
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
        }
    }
    return null
}