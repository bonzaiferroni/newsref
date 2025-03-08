package newsref.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import newsref.db.services.*
import newsref.model.Api
import newsref.server.utilities.*
import kotlin.time.Duration.Companion.days

fun Routing.serveChapters(service: ChapterDtoService = ChapterDtoService()) {
    getById(Api.Chapters) { id, endpoint ->
        val chapter = service.readChapter(id)
        call.respondOrNotFound(chapter)
    }

    getByPath(Api.Chapters) {
        val startInstant = it.start.readFromCallOrNull(call) ?: (Clock.System.now() - 7.days)
        val chapters = service.readTopChapters(startInstant)
        call.respond(chapters)
    }

    getByPath(Api.ChapterSources) {
        val pageId = it.pageId.readFromCallOrNull(call) ?: error("missing page id")
        val chapterId = it.chapterId.readFromCallOrNull(call) ?: error("missing chapter id")
        val chapterSource = service.readChapterSource(chapterId, pageId)
        call.respondOrNotFound(chapterSource)
    }
}

suspend fun <T> RoutingCall.respondOrNotFound(value: T?) {
    if (value != null) {
        respond(value)
    } else {
        respond(HttpStatusCode.NotFound)
    }
}