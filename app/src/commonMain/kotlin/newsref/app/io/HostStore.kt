package newsref.app.io

import kotlinx.datetime.*
import newsref.app.model.*
import newsref.model.Api
import newsref.model.dto.*

class HostStore(private val client: ApiClient = globalApiClient) {
    suspend fun readHosts(): List<Host> = client.get(Api.hostEndpoint.path)
    suspend fun readHost(hostId: Int): Host = client.getById(hostId, Api.hostEndpoint)
}