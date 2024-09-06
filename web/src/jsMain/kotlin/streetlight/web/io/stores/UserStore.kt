package streetlight.web.io.stores

import streetlight.model.User
import streetlight.web.io.StoreClient

class UserStore(
    private val client: StoreClient,
) {
    suspend fun addUser(user: User): Int? = client.create("/users", user)
    suspend fun update(user: User): Boolean = client.update("/users", user.id, user)
}