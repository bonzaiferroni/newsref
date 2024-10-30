package newsref.db.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import newsref.db.DbTest
import kotlin.test.Test

class SourceInfoTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = runBlocking {
		val service = SourceInfoService()
		val source = service.getSource(140)
		// println(source)
	}
}