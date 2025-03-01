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
    suspend inline fun <reified Received> getById(id: Long, url: String): Received =
        client.get("$baseUrl${url.replace(":id", id.toString())}").body()

    suspend inline fun <reified Received> get(url: String): Received = client.get("$baseUrl$url") {
        contentType(ContentType.Application.Json)
    }.body()
}

val globalApiClient = ApiClient("http://localhost:8080/")