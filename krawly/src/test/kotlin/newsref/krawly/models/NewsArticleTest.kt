package newsref.krawly.models

import newsref.db.utils.cacheSerializable
import kotlin.test.Test
import kotlin.test.assertNotNull
import newsref.db.utils.resourcePath
import newsref.krawly.utils.decodeNewsArticle
import newsref.krawly.utils.readArrayOrObject
import newsref.model.core.toUrl
import java.io.File

class NewsArticleTest {


    @Test
    fun `test parse real-world NewsArticle JSON`() {
        val resourcePath = "${resourcePath}/news_article_raw"
        val jsonFiles = File(resourcePath).listFiles { _, name -> name.endsWith(".json") } ?: return

        for (file in jsonFiles) {
            val jsonString = file.readText()
            val result = jsonString.readArrayOrObject()
            assertNotNull(result, "Parsed NewsArticle object from ${file.name} should not be null")
        }
    }

    @Test
    fun `test decode real-world NewsArticle JSON`() {
        val resourcePath = "${resourcePath}/news_article_raw"
        val jsonFiles = File(resourcePath).listFiles { _, name -> name.endsWith(".json") } ?: return

        for (file in jsonFiles) {
            val jsonString = file.readText()
            val result = jsonString.decodeNewsArticle()
            result?.cacheSerializable("http://example.com".toUrl(), "news_article_tests", file.name)
            assertNotNull(result, "Parsed NewsArticle object from ${file.name} should not be null")
        }
    }
}
