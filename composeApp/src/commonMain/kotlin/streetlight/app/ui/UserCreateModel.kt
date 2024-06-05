package streetlight.app.ui

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import streetlight.app.data.UserDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.User

class UserCreateModel(
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

    fun addUser() {
        screenModelScope.launch {
            val result = userDao.addUser(sv.user)
            sv = sv.copy(result = "id: $result", user = User())
        }
    }
}

data class CreateUserState(
    val user: User = User(),
    val result: String = ""
) : UiState