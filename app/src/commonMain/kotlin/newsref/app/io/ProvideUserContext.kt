package newsref.app.io

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.blip.controls.Button
import newsref.app.blip.controls.DialogOld
import newsref.app.blip.controls.Text
import newsref.app.blip.controls.TextField
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
            DialogOld(state.loginVisible, userContext::dismissLogin) {
                Column(
                    verticalArrangement = Blip.ruler.columnTight
                ) {
                    TextField(state.usernameOrEmail, userContext::setUsernameOrEmail)
                    TextField(state.password, userContext::setPassword)
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