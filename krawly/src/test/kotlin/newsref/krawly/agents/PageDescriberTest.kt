package newsref.krawly.agents

import kotlinx.coroutines.runBlocking
import newsref.db.services.ContentService
import kotlin.test.Test

class PageDescriberTest {
	@Test
	fun `explore code`() = runBlocking {
		val content = ContentService().readPageContentText(382)
		println(content)
	}
}