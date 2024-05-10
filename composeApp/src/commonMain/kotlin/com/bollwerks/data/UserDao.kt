package com.bollwerks.data

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserDao {
    suspend fun fetchMessage(): String {
        val response = web.get("http://localhost:8080")
        return response.bodyAsText()
    }

    suspend fun addUser(name: String, email: String): String {
        val response = web.post("http://localhost:8080/users") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                    {
                        "id": 0,
                        "name": "$name",
                        "email": "$email",
                        "password": "hunter2"
                    }
                    """.trimIndent()
            )
        }
        return response.status.toString()
    }
}
