package streetlight.app.data

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import streetlight.model.User

class LoginDao : ApiDao() {
    suspend fun login(username: String, password: String): String {
        val response = web.post("$address/login") {
            contentType(ContentType.Application.Json)
            setBody(User(name = username, password = password))
        }

        return response.bodyAsText()
    }
}