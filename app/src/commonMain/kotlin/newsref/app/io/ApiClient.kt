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
    suspend inline fun <reified Received> getById(
        id: Int,
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received =
        getById(id.toLong(), endpoint, *params)

    suspend inline fun <reified Received> getById(
        id: Long,
        endpoint: Endpoint,
        vararg params: Pair<String, String>
    ): Received = getRequest("$baseUrl${endpoint.clientIdTemplate.replace(":id", id.toString())}", *params)

    suspend inline fun <reified Received> get(
        url: String,
        vararg params: Pair<String, String>
    ): Received = getRequest("$baseUrl$url", *params)

    suspend inline fun <reified Received> getRequest(
        url: String,
        vararg params: Pair<String, String>
    ): Received = client.get(url) {
        contentType(ContentType.Application.Json)
        if (params.isNotEmpty()) {
            url { params.forEach { parameters.append(it.first, it.second) } }
        }
    }.body()
}

val globalApiClient = ApiClient("http://192.168.1.100:8080")