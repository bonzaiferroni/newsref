package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.EmbeddingService
import newsref.krawly.utils.AiClient
import newsref.krawly.utils.AiEmbeddingsRequest
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("SourceEmbedder")

class SourceEmbedder(
	private val client: AiClient,
	private val embeddingService: EmbeddingService = EmbeddingService()
) {
	fun start() {
		CoroutineScope(Dispatchers.IO).launch {
			console.logTrace("looking for sources")
			while (true) {
				val start = Clock.System.now()
				findEmbeddings()
				val delay = Clock.System.now() - start
				if (delay < 5.minutes) delay(5.minutes - delay)
			}
		}
	}

	private suspend fun findEmbeddings() {
//		val source = embeddingService.findNextJob()
//		val
//		val request = AiEmbeddingsRequest(
//			model = "text-embedding-3-small",
//			input =
//		)
	}
}