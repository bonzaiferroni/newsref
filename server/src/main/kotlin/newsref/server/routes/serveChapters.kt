package newsref.server.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.*
import newsref.model.Api
import kotlin.time.Duration.Companion.days

fun Routing.serveChapters(service: ChapterDtoService = ChapterDtoService()) {
    get(Api.chapterEndpoint.path) {
        val chapters = service.readTopChapters(7.days)
        call.respond(chapters)
    }
}