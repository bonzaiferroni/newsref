package newsref.krawly.utils

import newsref.model.core.toUrl
import kotlin.test.Test

class RobotsTest {
    @Test
    fun `getRobotsTxtUrl returns correct url`() {
        val url = "https://example.com/myExtraPath".toUrl()
        val expected = "https://example.com/robots.txt"
        assert(url.getRobotsTxtUrl().toString() == expected)
    }
}