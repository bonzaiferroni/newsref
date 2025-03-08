package newsref.app.io

import kotlinx.datetime.*
import newsref.app.model.*
import newsref.model.Api
import newsref.model.dto.*

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long): ChapterPackDto = client.getById(id, Api.Chapters)
    suspend fun readChapters(start: Instant): List<ChapterPackDto> = client.get(
        Api.Chapters,
        Api.Chapters.start.write(start),
    )
    suspend fun readChapterSource(chapterId: Long, pageId: Long): ChapterSource = client.get(
        Api.ChapterSources,
        Api.ChapterSources.chapterId.write(chapterId),
        Api.ChapterSources.pageId.write(pageId)
    )
}