package streetlight.web.io

import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.routing.Routing
import kotlinx.browser.window
import streetlight.model.dto.UserInfo
import streetlight.web.getCurrentRoute
import streetlight.web.getUrlFragment
import streetlight.web.io.stores.AppModel
import streetlight.web.subscribe

fun Container.userContext(appModel: AppModel, routing: Routing, content: Container.(UserInfo) -> Unit) {
    val route = window.location.href.getUrlFragment()
    var container: Div? = null
    appModel.userInfo.subscribe { userInfo ->
        container?.let {
            this@userContext.remove(it)
        }
        container = null
        if (userInfo != null) {
            val div = div(className = "user-context")
            console.log("retrieved UserInfo")
            content(div, userInfo)
            container = div
        } else {
            if (window.location.href.contains(route)) {
                console.log("unauthorized, providing login")
                routing.navigate("/login?next=${route}")
            } else {
                console.log("inactive route $route observed null userInfo")
            }
        }
    }
}