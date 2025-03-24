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
    getIdEndpoint(Api.GetChapterById) { id, endpoint ->
        service.readChapter(id)
    }

    getEndpoint(Api.Chapters) {
        val startInstant = it.start.readFromCallOrNull(call) ?: (Clock.System.now() - 7.days)
        service.readTopChapters(startInstant)
    }

    getEndpoint(Api.ChapterSources) {
        val pageId = it.pageId.readFromCallOrNull(call) ?: error("missing page id")
        val chapterId = it.chapterId.readFromCallOrNull(call) ?: error("missing chapter id")
        service.readChapterSource(chapterId, pageId)
    }
}

suspend fun <T> RoutingCall.respondOrNotFound(value: T?) {
    if (value != null) {
        respond(value)
    } else {
        respond(HttpStatusCode.NotFound)
    }
}