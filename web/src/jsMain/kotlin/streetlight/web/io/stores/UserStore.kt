package streetlight.web.io.stores

import streetlight.model.dto.UserInfo
import streetlight.model.dto.UserRequest
import streetlight.web.io.StoreClient
import streetlight.web.io.globalStoreClient

class UserStore (
    private val client: StoreClient = globalStoreClient,
) {
    suspend fun getUserInfo(): UserInfo? {
        println("Getting user info: ${client.loginInfo.username}")
        return client.post("/users", client.loginInfo.username)
    }
}