package newsref.db.models

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json
import newsref.db.utils.readArrayOrObject
import newsref.db.utils.resourcePath
import java.io.File

class NewsArticleTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test parse real-world NewsArticle JSON`() {
        val resourcePath = "${resourcePath}/json"
        val jsonFiles = File(resourcePath).listFiles { _, name -> name.endsWith(".json") } ?: return

        for (file in jsonFiles) {
            val jsonString = file.readText()
            val result = jsonString.readArrayOrObject()
            assertNotNull(result, "Parsed NewsArticle object from ${file.name} should not be null")
        }
    }
}
