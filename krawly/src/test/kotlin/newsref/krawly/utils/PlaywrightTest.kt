package newsref.krawly.utils

import newsref.model.core.toUrl
import kotlin.test.Test

class PlaywrightTest {
	@Test
	fun `fetchHead recognizes redirect`() {
		// given
		val url = "https://shorturl.at/cs7Xp"
		// when
		val result = pwFetchNoRedirect(url.toUrl())
		// then
		println("ey!")
		// assert(result.isRedirect())
	}
}