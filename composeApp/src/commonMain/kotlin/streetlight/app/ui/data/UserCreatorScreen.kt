package streetlight.app.ui.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.launch
import streetlight.app.chopui.Scaffold
import streetlight.app.io.UserDao
import streetlight.app.ui.core.DataCreator
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.User

class UserCreatorScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<UserCreatorModel>()
        val state by screenModel.state

        DataCreator(
            title = "Add User",
            item = state.user,
            isComplete = state.isComplete,
            result = state.result,
            onComplete = null,
            createData = screenModel::createUser,
            navigator = navigator,
        ) {
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
        }
    }
}

class UserCreatorModel(
    private val userDao: UserDao
) : UiModel<CreateUserState>(CreateUserState()) {

    fun updateName(name: String) {
        sv = sv.copy(user = sv.user.copy(name = name))
    }

    fun updateEmail(email: String) {
        sv = sv.copy(user = sv.user.copy(email = email))
    }

    fun updatePassword(password: String) {
        sv = sv.copy(user = sv.user.copy(password = password))
    }

    fun createUser() {
        screenModelScope.launch {
            val result = userDao.addUser(sv.user)
            sv = sv.copy(result = "id: $result", user = User())
        }
    }
}

data class CreateUserState(
    val user: User = User(),
    val isComplete: Boolean = false,
    val result: String = ""
) : UiState