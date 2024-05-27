package streetlight.app.data

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import streetlight.model.User

class UserDao : ApiDao() {
    suspend fun fetchMessage(): String {
        val response = web.get(address)
        return response.bodyAsText()
    }

    suspend fun addUser(user: User): String {
        val response = web.post("$address/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return response.status.toString()
    }
}
