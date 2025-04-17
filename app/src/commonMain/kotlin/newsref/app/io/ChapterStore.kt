package newsref.app.io

import pondui.io.ApiClient
import pondui.io.globalApiClient
import kotlinx.datetime.*
import newsref.model.Api

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long) = client.get(Api.Chapters.GetChapterById, id)
    suspend fun readChapters(start: Instant) = client.get(
        Api.Chapters,
        Api.Chapters.start.write(start),
    )
    suspend fun readChapterPage(chapterId: Long, pageId: Long) = client.get(
        Api.Chapters.Pages,
        Api.Chapters.Pages.chapterId.write(chapterId),
        Api.Chapters.Pages.pageId.write(pageId)
    )
    suspend fun readChapterPersons(chapterId: Long) = client.get(Api.Chapters.Persons, chapterId)
}