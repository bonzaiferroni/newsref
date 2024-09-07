package streetlight.web.io

import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.html.P
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.rest.BadRequest
import io.kvision.rest.Unauthorized
import io.kvision.routing.Routing
import streetlight.model.dto.UserInfo
import streetlight.web.content.loginPage
import streetlight.web.io.stores.UserStore
import streetlight.web.launchedEffect

fun Container.userContext(content: Container.(UserInfo) -> Unit) {
    val store = UserStore()
    launchedEffect {

        try {
            val userInfo = store.getUserInfo()
            if (userInfo != null) {
                console.log("User info: $userInfo")
                content(this@userContext, userInfo)
            } else {

            }
        } catch (e: Exception) {
            console.log()
            when (e) {
                is Unauthorized, is BadRequest -> {
                    console.log("unauthorized, providing login")
                    val div = Div()
                    this@userContext.add(div)
                    div.loginPage() {
                        this@userContext.remove(div)
                        this@userContext.userContext(content)
                    }
                }
                else -> {
                    div("Error providing user context: $e")
                }
            }
        }
    }
}