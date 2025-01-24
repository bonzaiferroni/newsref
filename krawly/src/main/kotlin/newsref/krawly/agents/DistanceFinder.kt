package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.environment
import newsref.db.globalConsole
import newsref.db.services.ContentService
import newsref.db.services.VectorService
import newsref.db.services.NoteService
import newsref.db.utils.profile
import newsref.krawly.clients.AiClient
import newsref.krawly.clients.EmbeddingRequest
import newsref.krawly.clients.EmbeddingResult
import newsref.model.data.Source
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("DistanceFinder")

class DistanceFinder(
	private val client: AiClient = AiClient(),
	private val vectorService: VectorService = VectorService(),
	private val contentService: ContentService = ContentService(),
	private val noteService: NoteService = NoteService()
) {

	fun start() {
		CoroutineScope(Dispatchers.IO).launch {
			console.logTrace("looking for sources")
			while (true) {
				findEmbeddings()
				delay(1.minutes)
			}
		}
	}

	private suspend fun findEmbeddings() {
		val token = environment["OPENAI_KEY"]
			?: throw IllegalStateException("Token missing from environment: OPENAI_KEY")
		val source = vectorService.findNextJob() ?: return
		val content = contentService.getSourceContentText(source.id)
		val model = "text-embedding-3-small"
		val request = EmbeddingRequest(
			model = model,
			input = content
		)
		val result: EmbeddingResult = client.request(request, "https://api.openai.com/v1/embeddings", token)
			?: return
		val vector = result.data.firstOrNull()?.embedding ?: throw IllegalStateException("no embedding found")
		vectorService.insertVector(source.id, model, vector)
		profile("distance_finder", true) {
			vectorService.generateDistances(source.id, model)
		}
	}
}

fun Source.toSourceUrl() = "http://localhost:3000/#/source/${this.id}"