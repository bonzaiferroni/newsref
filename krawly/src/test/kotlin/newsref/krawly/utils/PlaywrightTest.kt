package newsref.krawly.utils

import newsref.model.core.toUrl
import kotlin.test.Test

class PlaywrightTest {
	@Test
	fun `fetchHead recognizes redirect`() {
		// given
		val url = "https://tinyurl.com/4z6dd2hx"
		// when
		val result = pwFetchHead(url.toUrl())
		// then
		assert(result.isRedirect())
	}
}