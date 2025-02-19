package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import newsref.db.*
import newsref.db.model.Relevance
import newsref.db.services.*
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate
import newsref.model.Api.chapter
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ChapterWatcher")

class ChapterWatcher(
    env: Environment,
    private val sourceVectorService: SourceVectorService = SourceVectorService(),
    private val chapterComposerService: ChapterComposerService = ChapterComposerService(),
) {

    private val client = GeminiClient(env.read("GEMINI_KEY"))

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            console.logTrace("watching chapters")
            while (true) {
                findNullRelevance()
                delay(60.seconds)
            }
        }
    }

    private suspend fun findNullRelevance() {
        val topNullRelevance = chapterComposerService.readTopNullRelevance()
        if (topNullRelevance == null || topNullRelevance.second == 0L) {
            console.log("No null relevance")
            return
        }
        val (chapterId, nullRelevanceCount) = topNullRelevance
        val chapter = chapterComposerService.readChapter(chapterId)
            ?: error("chapter with id doesn't exist: $chapterId")
        val currentSources = chapterComposerService.readChapterSourceInfos(chapterId)
            .sortedBy { it.chapterSource.textDistance }
            .take(25)
//        val headlines = currentSources.filter { it.chapterSource.relevance == null }
//            .mapNotNull { signal -> signal.source.title?.let { "${signal.chapterSource.id}: $it" } }
//            .joinToString("\n")
        val headlines = currentSources.filter { it.chapterSource.relevance == null }
            .mapNotNull { signal -> signal.source.title }
            .joinToString("\n")
        if (headlines.isEmpty()) error("No null relevance found")

//        val prompt = chapter.title?.let {
//            promptTemplate(
//                "../docs/chapter_watcher-update_title.txt",
//                "title" to it,
//                "headlines" to headlines
//            )
//        } ?: promptTemplate(
//            "../docs/chapter_watcher-new_title.txt",
//            "headlines" to headlines
//        )

        val prompt = promptTemplate(
            "../docs/chapter_watcher-new_title.txt",
            "headlines" to headlines
        )

        val response: TitleResponse = client.requestJson(prompt) ?: return

        val newChapter = chapter.copy(
            title = response.title,
        )

        chapterComposerService.updateChapterDescription(newChapter)
//        val chapterSources = currentSources.map {
//            val chapterSource = it.chapterSource
//            val relevance = when {
//                response.relevantIds.contains(chapterSource.id) -> Relevance.Relevant
//                response.irrelevantIds.contains(chapterSource.id) -> Relevance.Irrelevant
//                else -> Relevance.Unsure
//            }
//            chapterSource.copy(relevance = relevance)
//        }
//        chapterComposerService.updateChapterSourceRelevance(chapterSources)

        console.log("Title: ${newChapter.title?.take(50)}")
        // console.log("Updated relevance: ${chapterSources.size}")
    }
}

@Serializable
data class TitleResponse(
    val title: String,
//    val relevantIds: Set<Long>,
//    val irrelevantIds: Set<Long>,
)