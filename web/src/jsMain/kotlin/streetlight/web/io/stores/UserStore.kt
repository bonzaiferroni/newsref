package streetlight.web.io.stores

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.UserInfo
import streetlight.model.dto.UserRequest
import streetlight.web.io.StoreClient
import streetlight.web.io.globalStoreClient

class UserStore (
    private val client: StoreClient = globalStoreClient,
) {
    suspend fun getUserInfo(): UserInfo? {
        return client.getAuth("/user")
    }
}