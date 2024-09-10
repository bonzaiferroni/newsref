package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.check.checkBox
import io.kvision.form.text.text
import io.kvision.html.*
import kotlinx.browser.window
import streetlight.web.*
import streetlight.web.ui.components.bindFrom
import streetlight.web.ui.components.bindTo
import streetlight.web.ui.components.row
import streetlight.web.ui.components.rows
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
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

    rows(group = true) {
        text {
            placeholder = "Username"
        }.bindTo(model::setUsername)
        text {
            placeholder = "Password"
            type = InputType.PASSWORD
        }.bindTo(model::setPassword)
        row(group = true) {
            button("Login").onClickLaunch {
                val success = model.login()
                if (success) {
                    console.log("loginPage: success")
                    onSuccess()
                } else {
                    console.log("loginPage: failed")
                }
            }
            button("Create User", style = ButtonStyle.SECONDARY) {
                onClick {
                    context.routing.navigate("/user/create")
                }
            }
        }
        checkBox(label = "Store credentials to stay logged in.", value = model.state.value.save)
            .bindTo(model::setSave)
        p().bindFrom(model.state) { it.msg }
    }

    launchedEffect {
        model.autoLogin()
    }
}

fun String?.getNextUrlValue(): String {
    return if (!this.isNullOrBlank() && !this.contains("login")) this else "/user"
}