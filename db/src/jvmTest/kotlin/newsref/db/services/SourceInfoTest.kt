package newsref.db.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import newsref.db.DbTest
import newsref.db.utils.profile
import kotlin.test.Test

class SourceInfoTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = runBlocking {
		val service = SourceInfoService()
		val source = profile("get_source", true) { service.getSource(140) }
		// println(source)
	}
}