package streetlight.web.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.SignUpInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.stores.UserStore

class SignUpModel(
    private val userStore: UserStore = UserStore(),
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    private val info = _state.value.info
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
        val error = userStore.createUser(info)
        val resultMessage = error ?: "User created."
        _state.value = _state.value.copy(resultMessage = resultMessage)
        return error == null
    }
}

data class SignUpState(
    val info: SignUpInfo = SignUpInfo(),
    val repeatPassword: String = "",
    val resultMessage: String = "",
) {
    val passwordMatch = info.password == repeatPassword
}