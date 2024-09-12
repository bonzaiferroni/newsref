package streetlight.web.ui.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.InputType
import io.kvision.html.button
import kotlinx.browser.window
import streetlight.web.Columns
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.getQueryParameter
import streetlight.web.launchedEffect
import streetlight.web.ui.components.bindTo
import streetlight.web.ui.components.col
import streetlight.web.ui.components.container
import streetlight.web.ui.components.row
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

    container(group = true) {
        row(group = true) {
            col {
                row {
                    col {
                        text {
                            placeholder = "Username"
                        }.bindTo(model::setUsername)
                        text {
                            placeholder = "Password"
                            type = InputType.PASSWORD
                        }.bindTo(model::setPassword)
                    }
                }
            }
            col {
            }
        }
        row(group = true, alignItems = AlignItems.STRETCH, direction = FlexDirection.ROW) {
            col {
                button("Login", className = Columns.half).onClickLaunch {
                    val success = model.login()
                    if (success) {
                        console.log("loginPage: success")
                        onSuccess()
                    } else {
                        console.log("loginPage: failed")
                    }
                }
            }
            col {
                button("Create User", className = Columns.half, style = ButtonStyle.SECONDARY).onClick {
                    context.navigate(Pages.signUp)
                }
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