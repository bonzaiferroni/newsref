package newsref.app.io

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip

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
                    verticalArrangement = Blip.ruler.columnTight
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
                        horizontalArrangement = Blip.ruler.rowTight
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