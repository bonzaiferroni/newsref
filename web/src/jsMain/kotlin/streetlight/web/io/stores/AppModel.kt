package streetlight.web.io.stores

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import streetlight.model.dto.UserInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.StoreClient
import streetlight.web.io.globalStoreClient

class AppModel (
    private val client: StoreClient = globalStoreClient,
): ViewModel() {

    private val _userInfo: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    val userInfo = _userInfo.asStateFlow()

    suspend fun requestUser() {
        _userInfo.value = client.getAuth("/user") ?: throw Exception("AppModel: No user info")
    }

    fun clearUser() {
        _userInfo.value = null
    }
}