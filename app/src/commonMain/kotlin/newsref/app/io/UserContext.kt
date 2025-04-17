package newsref.app.io

import androidx.lifecycle.viewModelScope
import pondui.ui.core.StateModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import newsref.app.KeyStore
import kabinet.model.User
import kabinet.model.LoginRequest
import kabinet.utils.obfuscate

class UserContext(
    private val keyStore: KeyStore = KeyStore(),
    private val userStore: UserStore = UserStore()
): StateModel<UserContextState>(UserContextState()) {

    var _cache = keyStore.readObjectOrNull() ?: UserContextCache()
    var cache
        get() = _cache
        set(value) = keyStore.writeObject(value).also { _cache = value }

    init {
        val (usernameOrEmail, stayLoggedIn, refreshToken) = cache
        if (usernameOrEmail != null && refreshToken != null) {
            login(LoginRequest(
                usernameOrEmail = usernameOrEmail,
                stayLoggedIn = stayLoggedIn,
                refreshToken = refreshToken
            ))
        }
        setState{ it.copy(
            isAnon = cache.usernameOrEmail == null,
            usernameOrEmail = cache.usernameOrEmail ?: "",
            stayLoggedIn = cache.stayLoggedIn
        )}
    }

    fun dismissLogin() {
        setState { it.copy(loginVisible = false) }
    }

    fun login() {
        if (!stateNow.loginReady) return
        login(LoginRequest(
            usernameOrEmail = stateNow.usernameOrEmail,
            stayLoggedIn = stateNow.stayLoggedIn,
            password = stateNow.password.obfuscate(),
        ))
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            val auth = userStore.login(LoginRequest(
                usernameOrEmail = request.usernameOrEmail,
                stayLoggedIn = request.stayLoggedIn,
                refreshToken = request.refreshToken,
                password = request.password
            ))
            if (auth != null) {
                cache = cache.copy(refreshToken = auth.refreshToken)
                keyStore.writeObject(cache)
                val user = userStore.readUser()
                setState { it.copy(user = user, loginVisible = user == null)}
            } else {
                setState { it.copy(loginVisible = true)}
            }
        }
    }

    fun setUsernameOrEmail(value: String) = setState { it.copy(usernameOrEmail = value) }
        .also { if (stateNow.saveLogin) cache = cache.copy(usernameOrEmail = value) }
    fun setPassword(value: String) = setState { it.copy(password = value) }
    fun setStayLoggedIn(value: Boolean) = setState { it.copy(stayLoggedIn = value) }
        .also { cache = cache.copy(stayLoggedIn = value) }
    fun setSaveLogin(value: Boolean) = setState { it.copy(saveLogin = value) }.also {
        if (value) {
            cache = cache.copy(usernameOrEmail = stateNow.usernameOrEmail)
        } else {
            cache = cache.copy(usernameOrEmail = null)
        }
    }
}

data class UserContextState(
    val user: User? = null,
    val isAnon: Boolean = true,
    val loginVisible: Boolean = true,
    val usernameOrEmail: String = "",
    val password: String = "",
    val saveLogin: Boolean = true,
    val stayLoggedIn: Boolean = false,
) {
    val isLoggedIn get() = user != null
    val loginReady get() = usernameOrEmail.isNotEmpty() && password.isNotEmpty()
}

@Serializable
data class UserContextCache(
    val usernameOrEmail: String? = null,
    val stayLoggedIn: Boolean = false,
    val refreshToken: String? = null,
)

const val USER_CACHE_KEY = "user_cache_key"