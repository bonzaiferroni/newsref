package streetlight.web.io.stores

import streetlight.model.dto.*
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient

class UserStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun getUser(): UserInfo = client.get("/user")

    // returns null if successful, otherwise returns an error message
    suspend fun createUser(info: SignUpRequest): SignUpResult = client.post("/user", info)
    suspend fun updateUser(info: EditUserRequest): Boolean = client.putRespondBool("/user", info)
    suspend fun getPrivateInfo(): PrivateInfo = client.get("/user/private")
}