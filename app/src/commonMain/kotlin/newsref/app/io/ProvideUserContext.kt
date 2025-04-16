package newsref.app.io

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import pondui.ui.theme.Pond
import pondui.ui.controls.*

@Composable
fun ProvideUserContext(
    userContext: UserContext = viewModel { UserContext() },
    block: @Composable () -> Unit
) {
    val state by userContext.state.collectAsState()

    CompositionLocalProvider(LocalUserContext provides userContext) {
        Box {
            block()
            FloatyBox(state.loginVisible, userContext::dismissLogin) {
                Column(
                    verticalArrangement = Pond.ruler.columnTight
                ) {
                    TextField(state.usernameOrEmail, userContext::setUsernameOrEmail)
                    TextField(
                        text = state.password,
                        onTextChange = userContext::setPassword,
                        hideCharacters = true,
                    )
                    LabelCheckbox(
                        value = state.saveLogin,
                        onValueChanged = userContext::setSaveLogin,
                        label = "Save username",
                    )
                    LabelCheckbox(
                        value = state.stayLoggedIn,
                        onValueChanged = userContext::setStayLoggedIn,
                        label = "Stay logged in",
                    )
                    Row(
                        horizontalArrangement = Pond.ruler.rowTight
                    ) {
                        Button(userContext::login) { Text("Log in") }
                        Button(userContext::dismissLogin) { Text("Cancel") }
                    }
                }
            }
        }
    }
}

val LocalUserContext = staticCompositionLocalOf<UserContext> {
    error("No Nav provided")
}