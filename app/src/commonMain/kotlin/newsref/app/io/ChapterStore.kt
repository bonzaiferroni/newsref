package newsref.app.io

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.model.Api
import newsref.model.dto.*
import kotlin.time.Duration

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long): ChapterPackDto = client.getById(id, Api.ChapterEndpoint)
    suspend fun readChapters(start: Instant): List<ChapterPackDto> = client.get(
        Api.ChapterEndpoint.path,
        Api.ChapterEndpoint.start.write(start),
    )
}