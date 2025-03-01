package newsref.server.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.*
import newsref.model.Api
import newsref.server.extensions.getIdOrThrow
import kotlin.time.Duration.Companion.days

fun Routing.serveChapters(service: ChapterDtoService = ChapterDtoService()) {
    get(Api.chapterEndpoint.serverIdTemplate) {
        val id = call.getIdOrThrow { it.toLongOrNull() }
        val chapter = service.readChapter(id) ?: error("unable to find chapter with id: $id")
        call.respond(chapter)
    }

    get(Api.chapterEndpoint.path) {
        val chapters = service.readTopChapters(7.days)
        call.respond(chapters)
    }
}