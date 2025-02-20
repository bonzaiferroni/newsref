package newsref.app.io

import newsref.model.Api
import newsref.model.dto.*

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapters() = client.get<List<ChapterPackDto>>(Api.chapterEndpoint.path)
}