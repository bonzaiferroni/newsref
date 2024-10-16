package newsref.krawly.models

import newsref.db.utils.cacheSerializable
import kotlin.test.Test
import kotlin.test.assertNotNull
import newsref.db.utils.resourcePath
import newsref.krawly.utils.decodeNewsArticle
import newsref.model.core.toUrl
import java.io.File

class NewsArticleTest {

    @Test
    fun `test decode real-world NewsArticle JSON`() {
        val resourcePath = "${resourcePath}/news_article_raw"
        val jsonFiles = File(resourcePath).listFiles { _, name -> name.endsWith(".json") } ?: return

        val nullSet = mutableSetOf<String>()
        for (file in jsonFiles) {
            val jsonString = file.readText()
            val result = jsonString.decodeNewsArticle()
            result?.cacheSerializable(file.name, "news_article_tests")
            if (result == null) {
                println("null NewsArticle: ${file.name}")
                nullSet += file.name
            }
        }

        assert(jsonFiles.isEmpty() || nullSet.size < jsonFiles.size / 2)
    }
}
