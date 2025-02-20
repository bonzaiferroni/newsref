package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.SourceService
import newsref.model.Api
import newsref.model.core.NewsSpan
import newsref.server.extensions.getIdOrThrow

fun Routing.sourceRouting(service: SourceService = SourceService()) {

	get(Api.feedEndpoint.serverIdTemplate) {
		val span = call.getIdOrThrow() {
			NewsSpan.entries.toTypedArray().getOrNull(it.toIntOrNull() ?: NewsSpan.WEEK.ordinal)
		}
		val sources = service.getTopSources(span.duration, 20)
		call.respond(sources)
	}

	get(Api.sourceEndpoint.serverIdTemplate) {
		val id = call.getIdOrThrow { it.toLongOrNull() }
		val source = service.getSourceCollection(id)
		if (source == null) {
			call.respond(HttpStatusCode.NotFound)
		} else {
			call.respond(source)
		}
	}
}