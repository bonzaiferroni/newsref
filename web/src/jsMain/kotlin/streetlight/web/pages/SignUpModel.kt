package streetlight.web.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.LoginInfo
import streetlight.model.dto.SignUpInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient
import streetlight.web.io.stores.UserStore

class SignUpModel(
    private val userStore: UserStore = UserStore(),
    private val client: ApiClient = globalApiClient,
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    private val info: SignUpInfo
        get() = _state.value.info
    val state = _state.asStateFlow()

    fun updateName(name: String) {
        _state.value = _state.value.copy(info = info.copy(name = name))
    }

    fun updateUsername(username: String) {
        _state.value = _state.value.copy(info = info.copy(username = username))
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(info = info.copy(password = password))
    }

    fun updateRepeatPassword(repeatPassword: String) {
        _state.value = _state.value.copy(repeatPassword = repeatPassword)
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(info = info.copy(email = email))
    }

    suspend fun signUp(): Boolean {
        val result = userStore.createUser(info)
        val resultMessage = result?.message ?: "I am error."
        _state.value = _state.value.copy(resultMessage = resultMessage)
        if (result?.success == true) {
            client.login(LoginInfo(info.username, info.password))
            return true
        } else {
            return false
        }
    }
}

data class SignUpState(
    val info: SignUpInfo = SignUpInfo(),
    val repeatPassword: String = "",
    val resultMessage: String = "",
) {
    val passwordMatch: Boolean
        get() = info.password == repeatPassword

    val validSignUp: Boolean
        get() = info.validSignUp && passwordMatch
}