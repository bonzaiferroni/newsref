package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.SourceInfoService
import newsref.model.Api
import newsref.server.extensions.getIdOrThrow
import kotlin.time.Duration.Companion.days

fun Routing.sourceRouting(service: SourceInfoService = SourceInfoService()) {

	get(Api.source.path) {
		val sources = service.getTopSources(7.days, 20)
		call.respond(sources)
	}

	get(Api.source.serverIdTemplate) {
		val id = call.getIdOrThrow { it.toLongOrNull() }
		val source = service.getSource(id)
		if (source == null) {
			call.respond(HttpStatusCode.NotFound)
		} else {
			call.respond(source)
		}
	}
}