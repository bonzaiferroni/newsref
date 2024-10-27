package newsref.server.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.FeedSourceService
import newsref.model.Api

fun Routing.feedSourceRouting() {
	val service = FeedSourceService()
	get(Api.feedSource.path) {
		val sources = service.getTopSources()
		call.respond(sources)
	}

	get(Api.feedSource.serverIdTemplate) {
//		val id = call.getIdOrThrow { it.toLongOrNull() }
//		val source = service.getSourceById(id)
//		if (source == null) {
//			call.respond(HttpStatusCode.NotFound)
//		} else {
//			call.respond(source)
//		}
	}
}