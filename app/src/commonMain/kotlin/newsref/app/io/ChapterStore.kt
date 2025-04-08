package newsref.app.io

import kotlinx.datetime.*
import newsref.model.Api

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long) = client.get(Api.Chapters.GetChapterById, id)
    suspend fun readChapters(start: Instant) = client.get(
        Api.Chapters,
        Api.Chapters.start.write(start),
    )
    suspend fun readChapterPage(chapterId: Long, pageId: Long) = client.get(
        Api.ChapterPages,
        Api.ChapterPages.chapterId.write(chapterId),
        Api.ChapterPages.pageId.write(pageId)
    )
}