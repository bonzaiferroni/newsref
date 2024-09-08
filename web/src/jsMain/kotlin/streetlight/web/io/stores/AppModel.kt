package streetlight.web.io.stores

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.UserInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient

class AppModel (
    private val client: ApiClient = globalApiClient,
    private val userStore: UserStore = UserStore(client),
): ViewModel() {

    private val _userInfo: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    val userFlow = _userInfo.asStateFlow()

    suspend fun requestUser(): UserInfo? {
        _userInfo.value = userStore.getUser()
        return _userInfo.value
    }

    fun logout() {
        client.logout()
        _userInfo.value = null
    }
}