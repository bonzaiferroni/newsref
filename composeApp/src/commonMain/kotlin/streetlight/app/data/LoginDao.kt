package streetlight.app.data

import io.ktor.client.statement.bodyAsText
import streetlight.model.User

class LoginDao(
    private val web: WebClient,
) {
    suspend fun login(username: String, password: String): String {
        val response = web.post("/login", User(name = username, password = password))

        return response.bodyAsText()
    }
}