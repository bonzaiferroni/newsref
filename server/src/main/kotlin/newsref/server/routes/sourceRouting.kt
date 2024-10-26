package newsref.server.routes

import io.ktor.server.routing.*
import newsref.db.services.FeedSourceService
import newsref.model.Api

fun Routing.sourceRouting() {
	val service = FeedSourceService()
	get(Api.source.path) {
//		val sources = service.getTopSources(25, 1.days)
//		call.respond(sources)
	}

	get(Api.source.serverIdTemplate) {
//		val id = call.getIdOrThrow { it.toLongOrNull() }
//		val source = service.getSourceById(id)
//		if (source == null) {
//			call.respond(HttpStatusCode.NotFound)
//		} else {
//			call.respond(source)
//		}
	}
}