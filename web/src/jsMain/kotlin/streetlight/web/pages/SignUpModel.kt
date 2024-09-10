package streetlight.web.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.LoginRequest
import streetlight.model.dto.SignUpRequest
import streetlight.model.obfuscate
import streetlight.web.core.ViewModel
import streetlight.web.io.client.ApiClient
import streetlight.web.io.client.globalApiClient
import streetlight.web.io.stores.UserStore

class SignUpModel(
    private val userStore: UserStore = UserStore(),
    private val client: ApiClient = globalApiClient,
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    private var info: SignUpRequest
        get() = _state.value.info
        set(value) {
            _state.value = _state.value.copy(info = value)
        }
    val state = _state.asStateFlow()

    fun updateName(name: String) {
        info = info.copy(name = name)
    }

    fun updateUsername(username: String) {
        info = info.copy(username = username)
    }

    fun updatePassword(password: String) {
        info = info.copy(password = password)
    }

    fun updateRepeatPassword(repeatPassword: String) {
        _state.value = _state.value.copy(repeatPassword = repeatPassword)
    }

    fun updateEmail(email: String) {
        info = info.copy(email = email)
    }

    suspend fun signUp(): Boolean {
        val result = userStore.createUser(info.copy(password = info.password.obfuscate()))
        val resultMessage = result.message
        _state.value = _state.value.copy(resultMessage = resultMessage)
        if (result.success) {
            client.coldLogin(LoginRequest(info.username, info.password))
            return true
        } else {
            return false
        }
    }
}

data class SignUpState(
    val info: SignUpRequest = SignUpRequest(),
    val repeatPassword: String = "",
    val resultMessage: String = "",
) {
    val passwordMatch: Boolean
        get() = info.password == repeatPassword

    val validSignUp: Boolean
        get() = info.validSignUp && passwordMatch
}