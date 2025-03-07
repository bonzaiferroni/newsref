package newsref.app.io

import kotlinx.datetime.Instant
import newsref.app.model.*
import newsref.model.Api

class HostStore(private val client: ApiClient = globalApiClient) {
    suspend fun readHosts(): List<Host> = client.get(Api.Hosts.path)
    suspend fun readHost(hostId: Int): Host = client.getById(hostId, Api.Hosts)
    suspend fun readFeedSources(hostId: Int, start: Instant): List<SourceBit> = client.getById(
        hostId, Api.Hosts.Sources,
        Api.Hosts.Sources.start.write(start)
    )
}