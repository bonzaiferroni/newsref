package streetlight.app.ui.login

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.LoginDao
import streetlight.app.data.web
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState

class LoginModel(
    private val loginDao: LoginDao
) : UiModel<LoginState>(LoginState()) {
    fun updateUsername(username: String) {
        sv = sv.copy(username = username)
    }

    fun updatePassword(password: String) {
        sv = sv.copy(password = password)
    }

    fun login() {
        screenModelScope.launch(Dispatchers.IO) {
            val result = loginDao.login(sv.username, sv.password)
            sv = sv.copy(result = result)
        }
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val result: String = "",
) : UiState