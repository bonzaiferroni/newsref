package streetlight.web.io

import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.routing.Routing
import kotlinx.browser.window
import streetlight.model.dto.UserInfo
import streetlight.web.core.PortalEvents
import streetlight.web.getCurrentRoute
import streetlight.web.getUrlFragment
import streetlight.web.io.stores.AppModel
import streetlight.web.launchedEffect
import streetlight.web.subscribe

fun Container.userContext(
    appModel: AppModel,
    routing: Routing,
    content: Container.(UserInfo) -> Unit
) {
    val route = window.location.href.getUrlFragment()
    launchedEffect {
        val userInfo = appModel.requestUser()
        if (userInfo != null) {
            console.log("userContext: retrieved UserInfo")
            content(this@userContext, userInfo)
        } else {
            console.log("userContext: unauthorized, providing login")
            routing.navigate("/login?next=${route}")
        }
    }
}