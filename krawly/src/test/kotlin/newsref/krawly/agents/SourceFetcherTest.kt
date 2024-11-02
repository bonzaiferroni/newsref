package newsref.krawly.agents

import kotlinx.coroutines.runBlocking
import newsref.krawly.SpiderWeb
import newsref.model.core.toCheckedUrl
import newsref.model.core.toUrl
import newsref.model.data.FetchStrategy.BASIC
import kotlin.test.Test

class SourceFetcherTest {
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