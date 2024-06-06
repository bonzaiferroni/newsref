package streetlight.app.ui.login

import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.io.ApiClient
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState

class LoginModel(
    private val apiClient: ApiClient
) : UiModel<LoginState>(LoginState()) {
    fun updateUsername(username: String) {
        sv = sv.copy(username = username)
    }

    fun updatePassword(password: String) {
        sv = sv.copy(password = password)
    }

    fun login() {
        screenModelScope.launch(Dispatchers.IO) {
            val response = apiClient.login(sv.username, sv.password)
            if (response.status != HttpStatusCode.OK) {
                return@launch
            }
            sv = sv.copy(loggedIn = true)
        }
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val loggedIn: Boolean = false,
) : UiState