package newsref.db.services

import kotlinx.coroutines.runBlocking
import newsref.db.DbTest
import newsref.db.utils.profile
import kotlin.test.Test

class PageInfoTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = runBlocking {
		val service = PageService()
		val source = profile("get_source", true) { service.getSourceCollection(140) }
		// println(source)
	}
}