package newsref.krawly.agents

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import newsref.db.globalConsole
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("NoteMaker")

class NoteMaker {

	private val ktor = HttpClient(CIO)

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("looking for sources")
				makeNotes()
				console.logTrace("sleeping")
				delay((10..15).random().seconds)
			}
		}
	}

	suspend fun makeNotes() {
//		val result = ktor.post("http://localhost:8000/v1/completions") {
//			setBody(AiRequest(
//				model = "Qwen/Qwen2.5-1.5B-Instruct",
//				prompt = "San Francisco is a",
//				max_tokens = 7,
//				temperature = .5,
//			))
//		}
//		println(result.bodyAsText())
	}
}