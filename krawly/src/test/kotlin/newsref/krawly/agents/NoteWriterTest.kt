package newsref.krawly.agents

import kotlinx.coroutines.runBlocking
import newsref.krawly.clients.AiClient
import kotlin.test.Test

class NoteWriterTest {

	private val client = AiClient()

	@Test
	fun `explore code`() = runBlocking {
//		val result = client.ask(
//			"how ye doing, matey"
//		)
//		println(result)
		// println(result.choices.first().text)
	}
}