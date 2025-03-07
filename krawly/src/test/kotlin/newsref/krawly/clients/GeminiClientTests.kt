package newsref.krawly.clients

import kotlinx.coroutines.runBlocking
import newsref.db.readEnvFromDirectory
import org.junit.jupiter.api.Test

class GeminiClientTests {
    @Test
    fun `Test embedding generation`() = runBlocking {
        val client = GeminiClient(readEnvFromDirectory("../.env").read("GEMINI_KEY_RATE_LIMIT_A"))
        val embedding = client.generateEmbeddings("This is a test")
        println(embedding?.take(10)?.joinToString(", ") { it.toString() })
    }
}