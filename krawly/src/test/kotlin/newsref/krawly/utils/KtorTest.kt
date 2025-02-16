package newsref.krawly.utils

import newsref.krawly.SpiderWeb
import newsref.krawly.agents.coroutineScope
import newsref.db.core.toUrl
import newsref.db.model.FetchStrategy
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class KtorTest {
	@Test
	fun `fetch returns pageHref of landing page`() = coroutineScope {
		val url = "https://tinyurl.com/4z6dd2hx".toUrl()
		val result = SpiderWeb().fetch(url, FetchStrategy.BASIC)
		assertNotNull(result.pageHref)
		assertNotEquals(url.href, result.pageHref)
	}
}