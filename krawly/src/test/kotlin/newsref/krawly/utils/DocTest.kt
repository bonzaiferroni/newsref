package newsref.krawly.utils

import newsref.db.utils.RESOURCE_PATH
import java.io.File
import kotlin.test.Test

class DocTest {

	private val docs by lazy {
		val resourcePath = "$RESOURCE_PATH/content"
		val htmlFiles = File(resourcePath).listFiles { _, name -> name.endsWith(".html") }
			?: throw Exception("oh no")
		htmlFiles.take(100).map { it.readText().contentToDoc() }
	}

	@Test
	fun `explore code`() {
		val results = mutableMapOf<String, Int>()
		for (doc in docs) {
			val result = doc.findFirstOrNull("html")?.attributes?.get("lang").toString()
			results[result] = results.getOrPut(result) { 0 } + 1
		}
		for ((result, count) in results) {
			println("$result: $count")
		}
	}
}