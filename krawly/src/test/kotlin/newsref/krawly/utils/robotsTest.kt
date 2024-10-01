package newsref.krawly.utils

import kotlin.test.Test

class RobotsTest {
    @Test
    fun `getRobotsTxtUrl returns correct url`() {
        val url = "https://example.com/myExtraPath"
        val expected = "https://example.com/robots.txt"
        assert(url.getRobotsTxtUrl() == expected)
    }
}