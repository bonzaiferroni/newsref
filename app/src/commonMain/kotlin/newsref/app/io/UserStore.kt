package newsref.app.io

import newsref.app.model.*
import newsref.model.Api
import newsref.model.data.LoginRequest

class UserStore : ApiStore() {

    suspend fun login(request: LoginRequest): Auth? = client.login(request)

    suspend fun readUser(): User = client.getSameData(Api.GetUser)

    // returns null if successful, otherwise returns an error message
    // suspend fun createUser(info: SignUpRequest): SignUpResult = client.post(Api.user, info)
    // suspend fun updateUser(info: EditUserRequest): Boolean = client.put(Api.user, info)
    // suspend fun getPrivateInfo(): PrivateInfo = client.get(Api.privateInfo)
}