package newsref.krawly.agents

import kotlinx.coroutines.runBlocking
import newsref.db.core.toUrl
import newsref.db.model.Host
import kotlin.test.Test

class TweetReaderTest {
	@Test
	fun `explore code`() = runBlocking {
		val url = "https://x.com/JDVance/status/1833148904864465117".toUrl()
		val host = Host(core = "x.com", domains = setOf("x.com"), score = 0)
//		val page = TweetReader(SpiderWeb()).read(url, host)
//		println(page?.contents?.firstOrNull())
	}
}