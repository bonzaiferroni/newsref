package newsref.krawly.agents

import newsref.db.services.ChapterComposerService
import newsref.krawly.DbTest
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate
import kotlin.test.Test

class ChapterWatcherTests() : DbTest(true) {

    private val chapterWatcher = ChapterWatcher(env)

    @Test
    fun `findRelevance completes`() = coroutineScope {
        chapterWatcher.findRelevance()
        assert(true)
    }
}