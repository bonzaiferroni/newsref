package streetlight.app.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import streetlight.app.data.UserDao
import streetlight.model.User

class CreateUserModel(private val userDao: UserDao) : ScreenModel {
    private val _state = mutableStateOf(CreateUserState())
    private var sv
        get() = _state.value
        set(value) { _state.value = value }
    val state: State<CreateUserState> = _state

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
            sv = sv.copy(result = result, user = User())
        }
    }
}

data class CreateUserState(
    val user: User = User(),
    val result: String = ""
)