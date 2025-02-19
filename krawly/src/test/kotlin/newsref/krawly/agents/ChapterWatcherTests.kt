package newsref.krawly.agents

import newsref.db.services.ChapterComposerService
import newsref.krawly.DbTest
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate
import kotlin.test.Test

class ChapterWatcherTests() : DbTest(true) {

    private val chapterComposerService: ChapterComposerService = ChapterComposerService()

    private val client = GeminiClient(env.read("GEMINI_KEY"))

    @Test
    fun `generate chapter title`() = coroutineScope {
        val topNullRelevance = chapterComposerService.readTopNullRelevance()
        if (topNullRelevance == null || topNullRelevance.second == 0L) {
            println("No null relevance")
            return@coroutineScope
        }
        val (chapterId, nullRelevanceCount) = topNullRelevance
        val chapter = chapterComposerService.readChapter(chapterId)
            ?: error("chapter with id doesn't exist: $chapterId")
        val currentSources = chapterComposerService.readChapterSourceInfos(chapterId)
            .sortedBy { it.chapterSource.textDistance }
            .take(25)
        val headlines = currentSources.filter { it.chapterSource.relevance == null }
            .mapNotNull { signal -> signal.source.title }
            .joinToString("\n")
        if (headlines.isEmpty()) error("No null relevance found")
        val prompt = promptTemplate(
            "../docs/chapter_watcher-new_title.txt",
            "headlines" to headlines
        )
        println(prompt)

        val response: TitleResponse = client.requestJson(prompt) ?: return@coroutineScope

        val newChapter = chapter.copy(
            title = response.title,
        )
        assert(newChapter.title != null)
    }
}