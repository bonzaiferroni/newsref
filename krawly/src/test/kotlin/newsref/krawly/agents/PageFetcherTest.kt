package newsref.krawly.agents

import kotlinx.coroutines.runBlocking
import newsref.krawly.SpiderWeb
import newsref.db.core.toCheckedUrl
import newsref.db.core.toUrl
import newsref.db.model.FetchStrategy.BASIC
import kotlin.test.Test

class PageFetcherTest {
	@Test
	fun `explore code`() = runBlocking {
		val web = SpiderWeb()
		val leadUrl = "https://x.com/Liz_Cheney/status/1852292100844621974".toUrl()
		val twitterUrl = leadUrl.href.toCheckedUrl(setOf(), setOf()).let {
			"https://publish.twitter.com/oembed?url=${it.href.encodeForUrl()}".toUrl()
		}
		println("twitter url:\n$twitterUrl")
		val result = web.fetch(twitterUrl, BASIC)
		println(result.content)
	}
}