package newsref.db.services

import kotlinx.coroutines.runBlocking
import newsref.db.DbTest
import kotlin.test.Test

class SourceContentService : DbTest(true) {
	@Test
	fun `explore code`() = runBlocking {
		val content = ContentService().getSourceContent(1)
		println("content:\n\n${content}")
	}
}