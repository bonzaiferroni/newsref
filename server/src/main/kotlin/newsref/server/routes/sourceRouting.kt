package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.SourceInfoService
import newsref.model.Api
import newsref.server.extensions.getIdOrThrow
import kotlin.time.Duration.Companion.days

fun Routing.sourceRouting() {
	val service = SourceInfoService()
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