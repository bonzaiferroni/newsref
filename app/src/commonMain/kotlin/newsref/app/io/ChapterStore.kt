package newsref.app.io

import newsref.model.Api
import newsref.model.data.*

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapters() = client.get<List<Chapter>>(Api.chapter.path)
}