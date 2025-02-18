package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import newsref.db.*
import newsref.db.services.*
import newsref.krawly.clients.AiClient
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate
import java.io.File
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
                setTitle()
                delay(60.seconds)
            }
        }
    }

    private suspend fun setTitle() {
        val chapter = chapterComposerService.readTitleIsNull().maxByOrNull { it.score } ?: return
        // val model = sourceVectorService.readOrCreateModel(defaultModelName)
        val currentSources = chapterComposerService.readChapterSourceInfos(chapter.id)

        val headlines = currentSources.mapNotNull { it.source.title }.joinToString("\n")
        val response: TitleResponse = client.requestJson(
            promptTemplate("../docs/chapter_watcher-invocation.txt"),
            promptTemplate(
                "../docs/chapter_watcher-ask_title.txt",
                "headlines" to headlines
            )
        ) ?: return

        val newChapter = chapter.copy(
            title = response.title,
        )

        chapterComposerService.updateChapterDescription(newChapter)

        console.log("Title: ${newChapter.title?.take(50)}")
    }
}

@Serializable
data class TitleResponse(
    val title: String,
)