package newsref.web.io.stores

import newsref.model.Api
import newsref.model.dto.*
import newsref.web.io.client.ApiClient
import newsref.web.io.client.globalApiClient

class UserStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun getUser(): UserInfo = client.get(Api.user)

    // returns null if successful, otherwise returns an error message
    suspend fun createUser(info: SignUpRequest): SignUpResult = client.post(Api.user, info)
    suspend fun updateUser(info: EditUserRequest): Boolean = client.put(Api.user, info)
    suspend fun getPrivateInfo(): PrivateInfo = client.get(Api.privateInfo)
}