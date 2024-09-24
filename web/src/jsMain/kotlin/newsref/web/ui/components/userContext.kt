package newsref.web.ui.components

import io.kvision.core.Container
import kotlinx.browser.window
import newsref.model.dto.UserInfo
import newsref.web.*
import newsref.web.core.AppContext
import newsref.web.ui.pages.getNextUrlValue

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