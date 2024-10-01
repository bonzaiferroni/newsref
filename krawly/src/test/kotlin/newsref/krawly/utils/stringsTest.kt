package newsref.krawly.utils

import org.junit.jupiter.api.Test

class StringsTest {
    @Test
    fun `hasHostAndProtocolTest returns true positives`() {
        val urls = listOf(
            "https://www.cnn.com",
            "http://www.cnn.com",
            "https://www.cnn.com/health",
            "http://www.cnn.com/health",
            "https://www.cnn.com/health/",
            "http://www.cnn.com/health/",
            "https://www.cnn.com/health/?query=1",
            "http://www.cnn.com/health/?query=1",
            "https://www.cnn.com/health/?query=1#section",
            "http://www.cnn.com/health/?query=1#section",
        )
        urls.forEach {
            assert(it.hasHostAndProtocol())
        }
    }

    @Test
    fun `hasHostAndProtocolTest returns true negatives`() {
        val urls = listOf(
            "www.cnn.com",
            "cnn.com",
            "www.cnn.com/health",
            "cnn.com/health",
            "www.cnn.com/health/",
            "cnn.com/health/",
            "www.cnn.com/health/?query=1",
            "cnn.com/health/?query=1",
            "www.cnn.com/health/?query=1#section",
            "/cnn.com/health/?query=1#section",
            "ftp://www.cnn.com/health"
        )
        urls.forEach {
            assert(!it.hasHostAndProtocol())
        }
    }
}