package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.ContentService
import newsref.db.services.EmbeddingService
import newsref.db.services.NoteService
import newsref.db.utils.format
import newsref.krawly.utils.AiClient
import newsref.krawly.utils.EmbeddingRequest
import newsref.krawly.utils.EmbeddingResult
import newsref.model.data.Source
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("SourceEmbedder")

class SourceEmbedder(
	private val client: AiClient = AiClient(),
	private val embeddingService: EmbeddingService = EmbeddingService(),
	private val contentService: ContentService = ContentService(),
	private val noteService: NoteService = NoteService()
) {

	val token = System.getenv("OPENAI_KEY")

	fun start() {
		CoroutineScope(Dispatchers.IO).launch {
			console.logTrace("looking for sources")
			while (true) {
				val start = Clock.System.now()
				findEmbeddings()
				val delay = Clock.System.now() - start
				if (delay < 1.minutes) delay(1.minutes - delay)
			}
		}
	}

	private suspend fun findEmbeddings() {
		val source = embeddingService.findNextJob() ?: return
		val content = contentService.getSourceContent(source.id)
		val model = "text-embedding-3-small"
		val request = EmbeddingRequest(
			model = model,
			input = content
		)
		val result: EmbeddingResult = client.request(request, "https://api.openai.com/v1/embeddings", token)
		val vector = result.data.firstOrNull()?.embedding ?: throw IllegalStateException("no embedding found")
		embeddingService.setEmbedding(source.id, vector)
		val userId = noteService.getUserId("EmbeddingBot") ?: return
		noteService.createNote(source.id, userId, "Source Content", content)
		val neighbors = embeddingService.getCosineNeighbors(source.id)
		val noteContent = neighbors.joinToString("\n") {
			"${it.cosineDistance.format(2)}: http://localhost:3000/#/source/${it.neighborId}"
		}
		noteService.createNote(source.id, userId, "Cosine Neighbors", noteContent)
		console.log("Cosine Distances: ${source.toSourceUrl()}\n$noteContent")
	}
}

fun Source.toSourceUrl() = "http://localhost:3000/#/source/${this.id}"