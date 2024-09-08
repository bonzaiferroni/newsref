package streetlight.web.io.stores

import io.kvision.rest.BadRequest
import streetlight.model.dto.SignUpInfo
import streetlight.model.dto.UserInfo
import streetlight.web.io.ApiClient
import streetlight.web.io.globalApiClient

class UserStore(
    private val client: ApiClient = globalApiClient,
) {
    suspend fun getUser(): UserInfo? = client.getAuth("/user")

    // returns null if successful, otherwise returns an error message
    suspend fun createUser(info: SignUpInfo): String?  {
        try {
            val result: Boolean? = client.post("/users", info)
            if (result == true) {
                return null
            }
        } catch (e: Exception) {
            console.log("userStore.createUser: $e.message")
            when (e) {
                is BadRequest -> {
                    e.message?.let { return it }
                }
            }
        }
        return "An error occurred 🚧"
    }
}