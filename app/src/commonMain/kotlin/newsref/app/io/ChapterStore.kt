package newsref.app.io

import newsref.model.Api
import newsref.model.dto.*

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long): ChapterPackDto = client.getById(id, Api.chapterEndpoint.clientIdTemplate)
    suspend fun readChapters(): List<ChapterPackDto> = client.get(Api.chapterEndpoint.path)
}