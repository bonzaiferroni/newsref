package newsref.app.io

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiClient(
    val baseUrl: String,
    val client: HttpClient = globalKtorClient
) {
    suspend inline fun <reified Received> get(url: String) = client.get("$baseUrl$url") {
        contentType(ContentType.Application.Json)
    }.body<Received>()
}

val globalApiClient = ApiClient("http://localhost:8080/")