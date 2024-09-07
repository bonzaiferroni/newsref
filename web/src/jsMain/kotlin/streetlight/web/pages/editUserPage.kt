package streetlight.web.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h3
import io.kvision.html.p
import io.kvision.panel.vPanel
import streetlight.web.Layout
import streetlight.web.components.row
import streetlight.web.components.rows
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.gap
import streetlight.web.io.userContext

fun Container.editUserPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        rows {
            h3("Edit user")
            row {
                button("back", style = ButtonStyle.SECONDARY) {
                    onClick {
                        context.routing.navigate("/user")
                    }
                }
                button("save") {
                    onClick {
                        context.routing.navigate("/user")
                    }
                }
            }
        }
    }
    return null
}