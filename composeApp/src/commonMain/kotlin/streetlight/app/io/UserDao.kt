package streetlight.app.io

import streetlight.model.core.User

class UserDao(
    private val client: ApiClient,
) {
    suspend fun addUser(user: User): Int {
        return client.create("/users", user)
    }

    suspend fun update(user: User): Boolean = client.update("/users", user.id, user)
}
