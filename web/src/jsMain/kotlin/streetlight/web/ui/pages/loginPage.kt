package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.InputType
import io.kvision.html.button
import io.kvision.html.div
import kotlinx.browser.window
import streetlight.web.Tailwind
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.getQueryParameter
import streetlight.web.launchedEffect
import streetlight.web.ui.components.bindTo
import streetlight.web.ui.models.LoginModel

fun Container.loginPage(context: AppContext): PortalEvents? {
    val nextUrl = window.location.href.getQueryParameter("next").getNextUrlValue()
    loginWidget(context) {
        context.routing.navigate(nextUrl)
    }
    return null
}

fun Container.loginWidget(context: AppContext, onSuccess: () -> Unit) {
    val model = LoginModel()

    div(className = Tailwind.column) {
        text {
            placeholder = "Username"
        }.bindTo(model::setUsername)
        text {
            placeholder = "Password"
            type = InputType.PASSWORD
        }.bindTo(model::setPassword)
        div(className = Tailwind.row) {
            button("Login", className = Tailwind.flex1).onClickLaunch {
                val success = model.login()
                if (success) {
                    console.log("loginPage: success")
                    onSuccess()
                } else {
                    console.log("loginPage: failed")
                }
            }

            button("Create User", className = Tailwind.flex1, style = ButtonStyle.SECONDARY).onClick {
                context.navigate(Pages.signUp)
            }
        }
    }

    launchedEffect {
        model.autoLogin()
    }
}

fun String?.getNextUrlValue(): String {
    return if (!this.isNullOrBlank() && !this.contains("login")) this else "/user"
}