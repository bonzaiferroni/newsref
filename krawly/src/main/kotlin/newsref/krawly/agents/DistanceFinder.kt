package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.services.ContentService
import newsref.db.services.VectorService
import newsref.db.services.VECTOR_MAX_CHARACTERS
import newsref.db.services.VECTOR_MIN_WORDS
import newsref.db.utils.profile
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("DistanceFinder")

class DistanceFinder(
	private val client: VectorClient = VectorClient(),
	private val vectorService: VectorService = VectorService(),
	private val contentService: ContentService = ContentService(),
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
		val source = vectorService.findNextJob() ?: return
		val content = contentService.readSourceContentText(source.id).take(VECTOR_MAX_CHARACTERS)
		if (content.length < VECTOR_MIN_WORDS) throw IllegalStateException("Content too small: ${content.length}/${source.contentCount}")
		val model = "text-embedding-3-small"
		val vector = client.fetchVector(source, model, content) ?: return
		vectorService.insertVector(source.id, model, vector)
		profile("distance_finder", true) {
			vectorService.generateDistances(source.id, model)
		}
	}
}