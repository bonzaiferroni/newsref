package newsref.db.services

import kotlinx.coroutines.runBlocking
import newsref.db.DbTest
import newsref.db.utils.profile
import kotlin.test.Test

class SourceInfoTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = runBlocking {
		val service = SourceService()
		val source = profile("get_source", true) { service.getSourceCollection(140) }
		// println(source)
	}
}