package newsref.app.io

import kotlinx.datetime.Instant
import newsref.model.Api
import newsref.model.data.Feed
import newsref.model.data.Host
import newsref.model.data.PageLite

class HostStore(private val client: ApiClient = globalApiClient) {
    suspend fun readPinnedHosts(pinnedIds: Set<Int>): List<Host> = client.getSameData(
        Api.Hosts,
        Api.Hosts.ids.write(pinnedIds)
    )
    suspend fun searchHosts(search: String): List<Host> = client.getSameData(
        Api.Hosts,
        Api.Hosts.search.write(search)
    )
    suspend fun readTopHosts(): List<Host> = client.getSameData(Api.Hosts)
    suspend fun readHost(hostId: Int): Host = client.getSameData(Api.Hosts.GetHostById, hostId)
    suspend fun readHostSources(hostId: Int, start: Instant): List<PageLite> = client.getSameData(
        Api.Hosts.GetHostPages,
        hostId,
        Api.Hosts.GetHostPages.start.write(start)
    )
    suspend fun readHostFeeds(core: String): List<Feed> = client.getSameData(
        Api.Hosts.GetHostFeeds,
        Api.Hosts.GetHostFeeds.core.write(core)
    )
}