package streetlight.app.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import streetlight.app.chopui.BoxScaffold

@Composable
fun LoginScreen() {
    val navigator = rememberNavigator()
    val screenModel = koinViewModel(LoginModel::class)
    val state by screenModel.state
    BoxScaffold(
        title = "Login",
        navigator = navigator
    ) {
        Column {
            TextField(
                value = state.username,
                onValueChange = screenModel::updateUsername,
                label = { Text("Username") }
            )
            TextField(
                value = state.password,
                onValueChange = screenModel::updatePassword,
                label = { Text("Password") }
            )
            Button(
                onClick = screenModel::login) {
                Text("Login")
            }
            Text("Logged in: ${state.loggedIn}")
        }
    }

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) {
            // onLogin?.let { it() }
        }
    }
}