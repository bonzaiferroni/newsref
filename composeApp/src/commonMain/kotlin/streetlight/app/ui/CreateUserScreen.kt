package streetlight.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.Scaffold

class CreateUserScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<CreateUserModel>()
        val state by screenModel.state
        Scaffold("Add User", navigator) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    TextField(
                        value = state.user.name,
                        onValueChange = screenModel::updateName,
                        label = { Text("Name") }
                    )
                    TextField(
                        value = state.user.email,
                        onValueChange = screenModel::updateEmail,
                        label = { Text("Email") }
                    )
                    TextField(
                        value = state.user.password,
                        onValueChange = screenModel::updatePassword,
                        label = { Text("Password") }
                    )
                    Button(onClick = screenModel::addUser) {
                        Text("Add User")
                    }
                    Text(state.result)
                }
            }
        }
    }
}