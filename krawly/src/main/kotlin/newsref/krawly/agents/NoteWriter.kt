package newsref.krawly.agents

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.services.NoteService
import newsref.db.utils.RESOURCE_PATH
import newsref.db.utils.jsonDecoder
import newsref.krawly.models.BotConfig
import newsref.krawly.utils.AiClient
import java.io.File
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("NoteWriter")

class NoteWriter(
	private val noteService: NoteService = NoteService(),
) {

	private val client = AiClient()
	private val bots = File("${RESOURCE_PATH}/config/bots.json").readText().let {
		jsonDecoder.decodeFromString<List<BotConfig>>(it)
	}

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			console.logTrace("looking for sources")
			while (true) {
				makeNotes()
			}
		}
	}

	suspend fun makeNotes() {
		val bot = bots.random()
		val userId = noteService.getUserId(bot.name) ?: return
		val source = noteService.findNextSource() ?: return
		val (title, content) = noteService.getContent(source.id)
			?: Pair("haiku", listOf("nevermind about the article, write a haiku"))
		val prompt = "title: $title\n\nbody:\n${content.joinToString { "\n\n" }}"
		val chat = client.createChat(defaultInvocation, defaultScript)
		val response = chat.ask(prompt) ?: return
		val note = noteService.createNote(source.id, userId, "bot note", response)
		console.log("${source.url}\n${note.body}")
	}
}

private const val defaultInvocation =
	"Respond in the voice of a pirate. " +
			// "Prioritize brevity over thoroughness. " +
			"Your name is Rustbeard, you are the first officer of a pirate ship."

private const val defaultScript =
	"The user will present you with a news article. " +
			"Write a short note about the article. " +
			"Your note can summarize the main ideas or be about a specific part. " +
			"Your note should be roughly 1/5th the length of the article. " // +
			// "Your note should be no less than 20 characters and no more than 1000. "