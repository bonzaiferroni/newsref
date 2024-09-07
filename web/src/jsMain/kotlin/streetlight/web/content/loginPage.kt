package streetlight.web.content

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.check.checkBox
import io.kvision.form.text.text
import io.kvision.html.InputType
import io.kvision.html.button
import io.kvision.html.p
import io.kvision.panel.vPanel
import io.kvision.state.bindTo
import kotlinx.coroutines.flow.MutableStateFlow
import streetlight.model.dto.LoginInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.globalStoreClient
import streetlight.web.io.stores.LocalStore
import streetlight.web.subscribe

fun Container.loginPage() {
    val model = LoginPageModel()

    vPanel(spacing = 10) {
        text {
            placeholder = "Username"
        }.bindTo(model.username)
        text {
            placeholder = "Password"
            type = InputType.PASSWORD
        }.bindTo(model.password)
        button("Login").onClickLaunch {
            model.login()
        }
        checkBox(label = "Store credentials to stay logged in.") {}.bindTo(model.save)
        val message = p()
        model.msg.subscribe {
            message.content = it
        }
    }
}

class LoginPageModel() : ViewModel() {
    val localStore = LocalStore()
    val save = MutableStateFlow(localStore.save ?: false)
    val msg = MutableStateFlow("Hello.")
    val username = MutableStateFlow(localStore.username ?: "")
    val password = MutableStateFlow(localStore.session?.let { "hunter2" })

    suspend fun login() {
        val loginInfo = LoginInfo(username = username.value, password = password.value)
        localStore.save = save.value
        if (save.value) {
            localStore.username = username.value
        }

        val result = globalStoreClient.login(loginInfo)
        if (result) {
            msg.value = "Login successful."
        } else {
            msg.value = "Login failed."
        }
    }
}