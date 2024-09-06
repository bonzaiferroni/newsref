package streetlight.web.io

import streetlight.model.User

class UserStore(
    private val client: StoreClient,
) {
    suspend fun addUser(user: User): Int? = client.create("/users", user)
    suspend fun update(user: User): Boolean = client.update("/users", user.id, user)
}