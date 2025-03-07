package newsref.app.io

import kotlinx.datetime.*
import newsref.app.model.*
import newsref.model.Api
import newsref.model.dto.*

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long): ChapterPackDto = client.getById(id, Api.ChapterEndpoint)
    suspend fun readChapters(start: Instant): List<ChapterPackDto> = client.get(
        Api.ChapterEndpoint.path,
        Api.ChapterEndpoint.start.write(start),
    )
    suspend fun readChapterSource(chapterId: Long, pageId: Long): ChapterSource = client.get(
        Api.ChapterSourceEndpoint.path,
        Api.ChapterSourceEndpoint.chapterId.write(chapterId),
        Api.ChapterSourceEndpoint.pageId.write(pageId)
    )
}