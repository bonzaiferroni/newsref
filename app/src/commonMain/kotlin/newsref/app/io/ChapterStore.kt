package newsref.app.io

import kotlinx.datetime.Clock
import newsref.model.Api
import newsref.model.dto.*
import kotlin.time.Duration

class ChapterStore(private val client: ApiClient = globalApiClient) {
    suspend fun readChapter(id: Long): ChapterPackDto = client.getById(id, Api.chapterEndpoint.clientIdTemplate)
    suspend fun readChapters(span: Duration): List<ChapterPackDto> = client.get(
        Api.chapterEndpoint.path,
        "start" to (Clock.System.now() - span).epochSeconds.toString()
    )
}