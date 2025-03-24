package newsref.server.routes

import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import newsref.db.services.*
import newsref.model.Api
import newsref.server.utilities.*
import kotlin.time.Duration.Companion.days

fun Routing.serveHosts(service: HostDtoService = HostDtoService()) {
    get(Api.Hosts) {
        val ids = it.ids.readFromCallOrNull(call)
        val search = it.search.readFromCallOrNull(call)
        if (ids != null) {
            service.readHosts(ids)
        } else if (search != null) {
            service.searchHosts(search)
        } else {
            service.readTopHosts()
        }
    }

    getId(Api.GetHostById) { id, endpoint ->
        service.readHost(id.toInt())
    }

    getId(Api.GetHostSources) { id, endpoint ->
        val start = endpoint.start.readFromCallOrNull(call) ?: (Clock.System.now() - 1.days)
        service.readSources(id.toInt(), start)
    }

    get(Api.GetHostFeeds) {
        val core = it.core.readFromCall(call)
        service.readFeeds(core)
    }
}