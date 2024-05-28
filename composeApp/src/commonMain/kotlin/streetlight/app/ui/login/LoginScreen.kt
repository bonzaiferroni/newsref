package streetlight.app.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.BoxScaffold

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<LoginModel>()
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
                Text(state.result)
            }
        }
    }
}