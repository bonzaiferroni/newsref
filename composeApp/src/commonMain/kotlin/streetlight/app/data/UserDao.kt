package streetlight.app.data

import io.ktor.client.statement.bodyAsText
import streetlight.model.User

class UserDao(
    private val web: WebClient,
) {
    suspend fun fetchMessage(): String {
        val response = web.get("")
        return response.bodyAsText()
    }

    suspend fun addUser(user: User): String {
        val response = web.post("/users", user)
        return response.status.toString()
    }
}
