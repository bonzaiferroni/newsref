package newsref.server.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.SourceInfoService
import newsref.model.Api
import kotlin.time.Duration.Companion.days

fun Routing.sourceRouting() {
	get(Api.source.path) {
		val service = SourceInfoService()
		val sources = service.getTopSources(25, 1.days)
		call.respond(sources)
	}
}