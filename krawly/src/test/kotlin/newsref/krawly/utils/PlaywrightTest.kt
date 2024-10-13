package newsref.krawly.utils

import newsref.model.core.toUrl
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaywrightTest {
	@Test
	fun `fetchHead recognizes redirect`() {
		// given
		// val url = "https://shorturl.at/cs7Xp"
		val url = "https://tinyurl.com/bdey5ajd"
		var location = "https://thebookofluke.com/p/notes-from-the-radical-center"
		// when
		val result = pwFetchRedirect(url.toUrl())
		// then
		assertEquals(result.redirectHref, location)
	}
}