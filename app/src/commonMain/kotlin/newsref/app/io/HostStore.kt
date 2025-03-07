package newsref.app.io

import kotlinx.datetime.*
import newsref.app.model.*
import newsref.model.Api
import newsref.model.dto.*

class HostStore(private val client: ApiClient = globalApiClient) {
    suspend fun readHosts(): List<Host> = client.get(Api.hostEndpoint.path)
}