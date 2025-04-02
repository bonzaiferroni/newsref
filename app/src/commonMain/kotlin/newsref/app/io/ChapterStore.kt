package newsref.app.io

import kotlinx.datetime.*
import newsref.app.model.*
import newsref.model.Api
import newsref.model.dto.*

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long) = client.get(Api.Chapters.GetChapterById, id)
    suspend fun readChapters(start: Instant): List<ChapterPackDto> = client.get(
        Api.Chapters,
        Api.Chapters.start.write(start),
    )
    suspend fun readChapterSource(chapterId: Long, pageId: Long): ChapterPage = client.getSameData(
        Api.ChapterPages,
        Api.ChapterPages.chapterId.write(chapterId),
        Api.ChapterPages.pageId.write(pageId)
    )
}