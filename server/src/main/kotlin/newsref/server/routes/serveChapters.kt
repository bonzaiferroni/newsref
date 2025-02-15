package newsref.server.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.*
import newsref.model.Api
import kotlin.time.Duration.Companion.days

fun Routing.serveChapters(service: ChapterService = ChapterService()) {
    get(Api.chapter.path) {
        val chapters = service.readChapters(7.days)
        call.respond(chapters)
    }
}