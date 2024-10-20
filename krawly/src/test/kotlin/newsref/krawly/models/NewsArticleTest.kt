package newsref.krawly.models

import newsref.db.utils.cacheSerializable
import kotlin.test.Test
import newsref.db.utils.RESOURCE_PATH
import newsref.krawly.utils.decodeNewsArticle
import java.io.File
import kotlin.system.measureTimeMillis

class NewsArticleTest {

    @Test
    fun `test decode real-world NewsArticle JSON`() {
        val resourcePath = "${RESOURCE_PATH}/news_article_raw"
        val jsonFiles = File(resourcePath).listFiles { _, name -> name.endsWith(".json") } ?: return

        val nullSet = mutableSetOf<String>()
        val timeTaken = measureTimeMillis {
            for (file in jsonFiles) {
                val jsonString = file.readText()
                val result = jsonString.decodeNewsArticle()
                result?.cacheSerializable(file.name, "news_article_tests")
                if (result == null) {
                    println("null NewsArticle: ${file.name}")
                    nullSet += file.name
                }
            }
        }
        val formattedSeconds = "%.1f".format(timeTaken / 1000.0)
        println("files: ${jsonFiles.size}, null: ${nullSet.size}, time: $formattedSeconds seconds")

        assert(jsonFiles.isEmpty() || nullSet.size < jsonFiles.size / 2)
    }
}
