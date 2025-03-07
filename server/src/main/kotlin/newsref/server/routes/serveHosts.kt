package newsref.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.services.*
import newsref.model.Api
import newsref.server.utilities.*
import kotlin.time.Duration.Companion.days

fun Routing.serveHosts(service: HostDtoService = HostDtoService()) {
    getByPath(Api.hostEndpoint) {
        val hosts = service.readTopHosts()
        call.respond(hosts)
    }

    getById(Api.hostEndpoint) {  id, endpoint ->
        val host = service.readHost(id.toInt())
        call.respondOrNotFound(host)
    }
}