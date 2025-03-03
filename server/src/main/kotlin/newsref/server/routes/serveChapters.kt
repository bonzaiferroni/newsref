package newsref.server.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.services.*
import newsref.model.Api
import newsref.server.extensions.getIdOrThrow
import newsref.server.utilities.getById
import newsref.server.utilities.getByPath
import newsref.server.utilities.readFromCall
import kotlin.time.Duration.Companion.days

fun Routing.serveChapters(service: ChapterDtoService = ChapterDtoService()) {
    getById(Api.ChapterEndpoint) { id, endpoint ->
        val chapter = service.readChapter(id) ?: error("unable to find chapter with id: $id")
        call.respond(chapter)
    }

    getByPath(Api.ChapterEndpoint) {
        val startInstant = it.start.readFromCall(call) ?: (Clock.System.now() - 7.days)
        val chapters = service.readTopChapters(startInstant)
        call.respond(chapters)
    }

    getByPath(Api.ChapterSourceEndpoint) {
        val pageId = it.pageId.readFromCall(call) ?: error("missing page id")
        val chapterId = it.chapterId.readFromCall(call) ?: error("missing chapter id")
        val chapterSource = service.readChapterSource(chapterId, pageId)
        call.respond(chapterSource)
    }
}