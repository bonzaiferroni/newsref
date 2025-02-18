package newsref.krawly.agents

import kotlinx.coroutines.*
import newsref.db.*
import newsref.db.services.*
import newsref.krawly.clients.AiClient
import java.io.File
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ChapterWatcher")

class ChapterWatcher(
    env: Environment,
    private val sourceVectorService: SourceVectorService = SourceVectorService(),
    private val chapterComposerService: ChapterComposerService = ChapterComposerService(),
) {

    private val aiClient = AiClient(
        url = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions",
        model = "gemini-2.0-flash",
        token = env.read("GEMINI_KEY")
    )

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            console.logTrace("watching chapters")
            while (true) {
                setTitle()
                delay(10.seconds)
            }
        }
    }

    private suspend fun setTitle() {
        val chapter = chapterComposerService.readTitleIsNull().maxByOrNull { it.score } ?: return
        val model = sourceVectorService.readOrCreateModel(defaultModelName)
        val currentSources = chapterComposerService.readChapterSourceInfos(chapter.id)

        val invocation = File("../docs/ChapterWatcher-invocation.txt").readText()
        val chat = aiClient.createChat(invocation)
        val headlines = currentSources.mapNotNull { it.source.title }.joinToString("\n")
        val question = "Here are a list of headlines from a current news event:\n${headlines}" +
                "\n\nIf you were generating a book chapter about this event, " +
                "what would be the title of the chapter? " +
                "Create the shortest title that captures the essence of the event, the most relevant specific details. " +
                "Only give the chapter title and nothing else, no quotation marks."
        val response = chat.ask(question)
        if (response == null) console.logError("Chapter title was null")

        val newChapter = chapter.copy(
            title = response ?: chapter.title,
        )

        chapterComposerService.updateChapterDescription(newChapter)

        console.log("Title: ${newChapter.title?.take(50)}")
    }
}