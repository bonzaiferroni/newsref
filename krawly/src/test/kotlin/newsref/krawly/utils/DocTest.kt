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
		
	}
}