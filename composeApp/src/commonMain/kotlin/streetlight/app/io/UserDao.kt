package streetlight.app.io

import streetlight.model.User

class UserDao(
    private val client: ApiClient,
) {
    suspend fun addUser(user: User): Int {
        return client.create("/users", user)
    }
}
