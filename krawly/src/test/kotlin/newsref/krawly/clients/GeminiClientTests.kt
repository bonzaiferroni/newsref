package newsref.krawly.clients

import kotlinx.coroutines.runBlocking
import newsref.db.readEnvFromDirectory
import newsref.krawly.DbTest
import newsref.krawly.agents.ArticleReader
import newsref.krawly.agents.TitleResponse
import org.junit.jupiter.api.Test

class GeminiClientTests : DbTest(true) {
    @Test
    fun `Test embedding generation`() = runBlocking {
        val client = GeminiClient(readEnvFromDirectory("../.env").read("GEMINI_KEY_RATE_LIMIT_A"))
        val embedding = client.generateEmbeddings("This is a test")
        println(embedding?.take(10)?.joinToString(", ") { it.toString() })
    }

    @Test
    fun `Test 2_0 flash`() = dbQuery {
        val env = readEnvFromDirectory("../.env")
        val client = GeminiClient(
            limitedToken = env.read("GEMINI_KEY_RATE_LIMIT_A"),
            model = "gemini-2.5-pro-exp-03-25"
        )

        val locationClient = LocationClient(env.read("GOOGLE_MAPS_KEY"))
        val articleReader = ArticleReader(client, locationClient)
        articleReader.readNextArticle()
        println("Finished")
    }
}