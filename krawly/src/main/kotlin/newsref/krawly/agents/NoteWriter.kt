package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.NoteService
import newsref.db.utils.RESOURCE_PATH
import newsref.db.utils.jsonDecoder
import newsref.krawly.models.AiConfig
import newsref.krawly.models.BotStatus
import newsref.krawly.utils.AiClient
import newsref.model.data.Source
import java.io.File
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("NoteWriter")

class NoteWriter(
	private val noteService: NoteService = NoteService(),
) {

	private val client = AiClient()
	private val bots = File("${RESOURCE_PATH}/config/bots.json").readText().let {
		jsonDecoder.decodeFromString<List<AiConfig>>(it)
	}

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			console.logTrace("looking for sources")
			while (true) {
				val start = Clock.System.now()
				makeNotes()
				val delay = Clock.System.now() - start
				if (delay < 5.minutes) delay(5.minutes - delay)
			}
		}
	}

	private suspend fun makeNotes() {
		val source = noteService.findNextSource() ?: return
		val (title, content) = noteService.getContent(source.id)
			?: Pair("haiku", listOf("nevermind about the article, write a haiku inspired by this url:\n${source.url}"))
		val body = content.joinToString("\n\n")
		val prompt = "# $title\n\n$body"
		console.log(prompt)
		val randomBots = bots.filter{ it.status == BotStatus.RANDOM}
		if (randomBots.isNotEmpty()) {
			val bot = randomBots.random()
			makeNote(bot, source, prompt)
		}
		val activeBots = bots.filter { it.status == BotStatus.ACTIVE }
		if (title != "haiku" && activeBots.isNotEmpty()) {
			for (bot in activeBots) {
				makeNote(bot, source, prompt)
			}
		}
	}

	private suspend fun makeNote(bot: AiConfig, source: Source, prompt: String) {
		val userId = noteService.getUserId(bot.name) ?: return
		val chat = client.createChat(bot, defaultScript)
		val response = chat.ask(prompt) ?: return
		val note = noteService.createNote(source.id, userId, "bot note", response)
		console.log("${bot.name}${source.url}\n${note.body}")
	}
}

private const val defaultInvocation =
	"Respond in the voice of a pirate. " +
			// "Prioritize brevity over thoroughness. " +
			"Your name is Rustbeard, you are the first officer of a pirate ship."

private const val defaultScript =
	"The user will present you with a news article. " +
			"Write a short note about the article. " +
			"Some of the content might not be from the article, try to ignore anything that seems out of place. " +
			"Feel free to use humor when the subject matter is not serious, otherwise treat it with appropriate gravity. " +
			"Your note can summarize the main ideas or be about a specific part. " +
			"Your note should be roughly 1/5th the length of the article. " // +
			// "Your note should be no less than 20 characters and no more than 1000. "