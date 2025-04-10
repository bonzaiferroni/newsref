package newsref.krawly.agents

import newsref.db.readEnvFromDirectory
import newsref.krawly.DbTest
import newsref.krawly.clients.GeminiClient
import kotlin.test.Test

class ChapterWatcherTests() : DbTest(true) {

    val token = readEnvFromDirectory("../.env").read("GEMINI_KEY_RATE_LIMIT_A")
    private val chapterPromoter = ChapterPromoter(GeminiClient(token, token))

    @Test
    fun `findRelevance completes`() = coroutineScope {
        // chapterPromoter.findRelevance()
        assert(true)
    }
}