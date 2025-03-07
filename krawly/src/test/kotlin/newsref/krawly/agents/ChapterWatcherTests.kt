package newsref.krawly.agents

import newsref.db.readEnvFromDirectory
import newsref.db.services.ChapterComposerService
import newsref.krawly.DbTest
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate
import kotlin.test.Test

class ChapterWatcherTests() : DbTest(true) {

    val token = readEnvFromDirectory("../.env").read("GEMINI_KEY_RATE_LIMIT_A")
    private val chapterWatcher = ChapterWatcher(GeminiClient(token, token))

    @Test
    fun `findRelevance completes`() = coroutineScope {
        chapterWatcher.findRelevance()
        assert(true)
    }
}