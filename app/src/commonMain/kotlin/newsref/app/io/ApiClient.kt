package newsref.app.io

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.StringValues

class ApiClient(
    val baseUrl: String,
    val client: HttpClient = globalKtorClient
) {
    suspend inline fun <reified Received> getById(id: Long, url: String): Received =
        client.get("$baseUrl${url.replace(":id", id.toString())}").body()

    suspend inline fun <reified Received> get(
        url: String,
        params: Map<String, String>? = null
    ): Received = client.get("$baseUrl$url") {
        contentType(ContentType.Application.Json)
        params?.let {
            url { params.forEach { parameters.append(it.key, it.value) } }
        }
    }.body()
}

val globalApiClient = ApiClient("http://localhost:8080/")