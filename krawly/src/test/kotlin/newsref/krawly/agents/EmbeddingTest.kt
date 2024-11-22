package newsref.krawly.agents

import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import newsref.krawly.clients.AiClient
import newsref.krawly.clients.HfClient
import kotlin.test.Test

class EmbeddingTest {
	@Test
	fun `explore code`() = runBlocking {
		val client = HfClient("BAAI/bge-large-en-v1.5")
//		val embeddings: FloatArray? = client.request(
//			request = HfEmbeddingsRequest(
//				inputs = "The quick brown fox jumped over the lazy dog"
//			)
//		)
//		println(embeddings)
	}
}
