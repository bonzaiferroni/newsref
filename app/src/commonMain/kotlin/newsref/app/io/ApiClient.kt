package newsref.app.io

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.StringValues
import newsref.model.Endpoint

class ApiClient(
    val baseUrl: String,
    val client: HttpClient = globalKtorClient
) {
    suspend inline fun <reified Received> getById(id: Long, endpoint: Endpoint): Received =
        client.get("$baseUrl${endpoint.serverIdTemplate.replace(":id", id.toString())}").body()

    suspend inline fun <reified Received> get(
        url: String,
        vararg params: Pair<String, String>
    ): Received = client.get("$baseUrl$url") {
        contentType(ContentType.Application.Json)
        if (params.isNotEmpty()) {
            url { params.forEach { parameters.append(it.first, it.second) } }
        }
    }.body()
}

val globalApiClient = ApiClient("http://localhost:8080/")