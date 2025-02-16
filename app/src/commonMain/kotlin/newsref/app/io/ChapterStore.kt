package newsref.app.io

import newsref.model.Api
import newsref.model.data.*
import newsref.model.dto.*

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapters() = client.get<List<ChapterDto>>(Api.chapter.path)
}