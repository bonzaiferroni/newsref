package streetlight.web.components

import io.kvision.core.Container
import kotlinx.browser.window
import streetlight.model.dto.UserInfo
import streetlight.web.*
import streetlight.web.core.AppContext
import streetlight.web.pages.getNextUrlValue

fun Container.userContext(
    context: AppContext,
    content: Container.(UserInfo) -> Unit
) {
    val route = window.location.href.getUrlFragment().getNextUrlValue()

    launchedEffect {
        val userInfo = context.model.requestUser()
        if (userInfo != null) {
            console.log("userContext: retrieved UserInfo")
            content(this@userContext, userInfo)
        } else {

            console.log("userContext: unauthorized, providing login")
            context.routing.navigate("/login?next=${route}")
        }
    }
}