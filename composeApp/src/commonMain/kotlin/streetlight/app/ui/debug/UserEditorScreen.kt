package streetlight.app.ui.debug

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.parameter.parametersOf
import streetlight.app.io.UserDao
import streetlight.app.ui.debug.controls.DataEditor
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.User

@Composable
fun UserEditorScreen(id: Int?, navigator: Navigator?) {
    val viewModel = koinViewModel(UserEditorModel::class) { parametersOf(id) }
    val state by viewModel.state

    DataEditor(
        title = "Add User",
        isComplete = state.isComplete,
        result = state.result,
        createData = viewModel::createUser,
        isCreate = id == null,
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

class UserEditorModel(
    private val userDao: UserDao
) : UiModel<UserEditorState>(UserEditorState()) {

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
            if (sv.user.id == 0) {
                val result = userDao.addUser(sv.user)
                sv = sv.copy(result = "id: $result")
            } else {
                val result = userDao.update(sv.user)
                sv = sv.copy(result = "result: $result")
            }
        }
    }
}

data class UserEditorState(
    val user: User = User(),
    val isComplete: Boolean = false,
    val result: String = ""
) : UiState