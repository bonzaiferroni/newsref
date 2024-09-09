package streetlight.web.io.stores

import io.kvision.rest.Unauthorized
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonNull.content
import streetlight.model.dto.UserInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient
import streetlight.web.io.userContext

class AppModel (
    private val client: ApiClient = globalApiClient,
    private val userStore: UserStore = UserStore(client),
): ViewModel() {

    private val _userInfo: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    val userFlow = _userInfo.asStateFlow()

    suspend fun requestUser(): UserInfo? {
        return try {
            val user = userStore.getUser()
            console.log("AppModel: userInfo retrieved, propagating")
            return user
        } catch (e: Unauthorized) {
            null
        }
    }

    fun logout() {
        client.logout()
        _userInfo.value = null
    }
}