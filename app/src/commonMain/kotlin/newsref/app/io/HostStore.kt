package newsref.app.io

import kotlinx.datetime.Instant
import newsref.app.model.*
import newsref.model.Api

class HostStore(private val client: ApiClient = globalApiClient) {
    suspend fun readPinnedHosts(pinnedIds: Set<Int>): List<Host> = client.get(
        Api.Hosts,
        Api.Hosts.ids.write(pinnedIds)
    )
    suspend fun searchHosts(search: String): List<Host> = client.get(
        Api.Hosts,
        Api.Hosts.search.write(search)
    )
    suspend fun readTopHosts(): List<Host> = client.get(Api.Hosts)
    suspend fun readHost(hostId: Int): Host = client.getById(hostId, Api.Hosts)
    suspend fun readHostSources(hostId: Int, start: Instant): List<SourceBit> = client.getById(
        hostId,
        Api.Hosts.Sources,
        Api.Hosts.Sources.start.write(start)
    )
    suspend fun readHostFeeds(core: String): List<Feed> = client.get(
        Api.Hosts.Feeds,
        Api.Hosts.Feeds.core.write(core)
    )
}