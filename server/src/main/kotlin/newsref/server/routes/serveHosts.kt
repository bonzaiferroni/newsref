package newsref.server.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import newsref.db.services.*
import newsref.model.Api
import newsref.server.utilities.*
import kotlin.time.Duration.Companion.days

fun Routing.serveHosts(service: HostDtoService = HostDtoService()) {
    getByPath(Api.Hosts) {
        val hosts = service.readTopHosts()
        call.respond(hosts)
    }

    getById(Api.Hosts) { id, endpoint ->
        val host = service.readHost(id.toInt())
        call.respondOrNotFound(host)
    }

    getById(Api.Hosts.Sources) { id, endpoint ->
        val start = endpoint.start.readFromCall(call) ?: (Clock.System.now() - 1.days)
        val sources = service.readSources(id.toInt(), start)
        call.respond(sources)
    }
}