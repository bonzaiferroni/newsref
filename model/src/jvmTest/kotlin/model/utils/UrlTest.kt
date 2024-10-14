package model.utils

import newsref.model.core.toUrl
import kotlin.test.Test

class UrlTest {

	@Test
	fun `isMaybeSibling returns true positive`() {
		val url1 = "http://axios.com/2024/3/20/taco-tuesday".toUrl()
		val url2 = "http://axios.com/2024/11/6/pizza".toUrl()
		assert(url1.isMaybeSibling(url2))
	}

	@Test
	fun `isMaybeSibling returns true negative`() {
		val url1 = "http://axios.com/2024/3/20/taco-tuesday".toUrl()
		val url2 = "http://axios.com/about".toUrl()
		assert(!url1.isMaybeSibling(url2))
	}
}