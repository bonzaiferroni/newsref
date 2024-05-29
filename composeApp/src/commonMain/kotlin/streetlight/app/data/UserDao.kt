package streetlight.app.data

import io.ktor.client.statement.bodyAsText
import streetlight.model.User

class UserDao(
    private val client: ApiClient,
) {
    suspend fun addUser(user: User): Int {
        return client.create("/users", user)
    }
}
