package streetlight.app.ui.data

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.UserDao
import streetlight.app.ui.core.DataCreator
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.User

@Composable
fun UserCreatorScreen() {
    val navigator = rememberNavigator()
    val viewModel = koinViewModel(UserCreatorModel::class)
    val state by viewModel.state

    DataCreator(
        title = "Add User",
        item = state.user,
        isComplete = state.isComplete,
        result = state.result,
        createData = viewModel::createUser,
        navigator = navigator,
    ) {
        TextField(
            value = state.user.name,
            onValueChange = viewModel::updateName,
            label = { Text("Name") }
        )
        TextField(
            value = state.user.email,
            onValueChange = viewModel::updateEmail,
            label = { Text("Email") }
        )
        TextField(
            value = state.user.password,
            onValueChange = viewModel::updatePassword,
            label = { Text("Password") }
        )
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
        viewModelScope.launch {
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