package streetlight.web.pages

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.check.checkBox
import io.kvision.form.text.text
import io.kvision.html.*
import io.kvision.state.bindTo
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import streetlight.model.dto.LoginInfo
import streetlight.web.*
import streetlight.web.components.bindTo
import streetlight.web.components.row
import streetlight.web.components.rows
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.core.ViewModel
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient
import streetlight.web.io.stores.LocalStore

fun Container.loginPage(context: AppContext): PortalEvents? {
    console.log("Login page loaded")
    val nextUrl = window.location.href.getQueryParameter("next") ?: "/user"
    loginWidget(context) {
        context.routing.navigate(nextUrl)
    }
    return null
}

fun Container.loginWidget(context: AppContext, onSuccess: () -> Unit) {
    val model = LoginWidgetModel()

    rows(group = true) {
        text {
            placeholder = "Username"
        }.bindTo(model.username)
        text {
            placeholder = "Password"
            type = InputType.PASSWORD
        }.bindTo(model.password)
        row(group = true) {
            button("Login").onClickLaunch {
                val success = model.login()
                if (success) {
                    console.log("loginPage success")
                    onSuccess()
                } else {
                    console.log("loginPage failed")
                }
            }
            button("Create User", style = ButtonStyle.SECONDARY) {
                onClick {
                    context.routing.navigate("/user/create")
                }
            }
        }
        checkBox(label = "Store credentials to stay logged in.", value = model.save.value)
            .bindTo(model.save)
        val message = p()
        model.msg.subscribe {
            message.content = it
        }
    }

    launchedEffect {
        model.autoLogin()
    }
}

class LoginWidgetModel(
    val client: ApiClient = globalApiClient
) : ViewModel() {
    val localStore = LocalStore()
    val save = MutableStateFlow(localStore.save ?: false)
    val msg = MutableStateFlow("Hello.")
    val username = MutableStateFlow(localStore.username ?: "")
    val password = MutableStateFlow("")

    init {
        save.subscribe { localStore.save = it }
    }

    suspend fun login(): Boolean {
        val loginInfo = LoginInfo(username = username.value, password = password.value)
        localStore.save = save.value
        if (save.value) {
            localStore.username = username.value
        }

        val result = client.login(loginInfo)
        if (result) {
            msg.value = "Login successful."
            return true
        } else {
            msg.value = "Login failed."
            return false
        }
    }

    suspend fun autoLogin() {
        console.log("loginPage.autoLogin: session token: ${localStore.session?.substring(0..10)}")
        val go = save.value && localStore.session != null
        console.log("loginPage.autoLogin: ${go}")
        if (go) {
            login()
        }
    }
}