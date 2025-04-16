package newsref.server.routes

import io.ktor.server.routing.*
import klutch.server.get
import klutch.server.getById
import klutch.server.readFromCallOrNull
import kotlinx.datetime.Clock
import newsref.db.services.*
import newsref.model.Api
import newsref.server.utilities.*
import kotlin.time.Duration.Companion.days

fun Routing.serveChapters(service: ChapterDtoService = ChapterDtoService()) {
    getById(Api.Chapters.GetChapterById) { id, endpoint ->
        service.readChapter(id)
    }

    get(Api.Chapters) {
        val startInstant = it.start.readFromCallOrNull(call) ?: (Clock.System.now() - 7.days)
        service.readTopChapters(startInstant)
    }

    get(Api.Chapters.Pages) {
        val pageId = it.pageId.readFromCallOrNull(call) ?: error("missing page id")
        val chapterId = it.chapterId.readFromCallOrNull(call) ?: error("missing chapter id")
        service.readChapterPage(chapterId, pageId)
    }

    getById(Api.Chapters.Persons) { chapterId, endpoint ->
        service.readChapterPersons(chapterId)
    }
}