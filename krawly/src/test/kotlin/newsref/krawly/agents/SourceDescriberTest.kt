package newsref.krawly.agents

import kotlinx.coroutines.runBlocking
import newsref.db.services.ContentService
import kotlin.test.Test

class SourceDescriberTest {
	@Test
	fun `explore code`() = runBlocking {
		val content = ContentService().getSourceContent(382)
		println(content)
	}
}