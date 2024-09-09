package streetlight.web.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.LoginRequest
import streetlight.model.obfuscate
import streetlight.web.core.ViewModel
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient
import streetlight.web.io.stores.LocalStore
import streetlight.web.subscribe

class LoginModel(
    private val client: ApiClient = globalApiClient,
    private val localStore: LocalStore = LocalStore()
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState(
        save = localStore.save ?: false,
        username = localStore.username ?: ""
    ))
    val state = _state.asStateFlow()

    init {
        state.subscribe { localStore.save = it.save }
    }

    fun setUsername(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun setPassword(password: String) {
        _state.value = _state.value.copy(password = password.obfuscate())
    }

    fun setSave(save: Boolean) {
        _state.value = _state.value.copy(save = save)
    }

    fun setMsg(msg: String) {
        _state.value = _state.value.copy(msg = msg)
    }

    suspend fun login(): Boolean {
        val (username, password) = state.value
        val loginRequest = LoginRequest(username = username, password = password)
        localStore.save = state.value.save
        if (state.value.save) {
            localStore.username = username
        }

        val result = client.coldLogin(loginRequest)
        if (result) {
            setMsg("Login successful.")
            return true
        } else {
            setMsg("Login failed.")
            return false
        }
    }

    suspend fun autoLogin() {
        val go = state.value.save && localStore.session != null
        if (go) {
            login()
        }
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val save: Boolean = false,
    val msg: String = "Hello."
)