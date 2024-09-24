package newsref.web.io.stores

import io.kvision.rest.Unauthorized
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import newsref.model.dto.UserInfo
import newsref.web.core.ViewModel
import newsref.web.io.client.ApiClient
import newsref.web.io.client.globalApiClient

class AppModel (
    private val client: ApiClient = globalApiClient,
    private val userStore: UserStore = UserStore(client),
): ViewModel() {

    private val _userInfo: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    val userFlow = _userInfo.asStateFlow()

    suspend fun requestUser(): UserInfo? {
        return try {
            console.log("AppModel: requesting userInfo")
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