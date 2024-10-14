package model.utils

import newsref.model.core.toCheckedUrl
import newsref.model.core.toUrlWithContext
import newsref.model.core.toUrl
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckedUrlTest {

    @Test
    fun remove_query_parameters_test() {
        val url = "http://example.com?param1=value1&param2=value2"
        val keepParams = setOf("param1")
        val expected = "http://example.com?param1=value1"
        assertEquals(expected, url.toCheckedUrl(emptySet(), emptySet()).toString())
    }

    @Test
    fun try_parse_relative_uri_test() {
        val urls = listOf(
            "/myPath",
            "myPath",
            "myPath?stripped=param"
        )
        val context = "http://example.com".toUrl()
        val expectedStr = "http://example.com/myPath"
        val expectedUri = expectedStr.toUrl()
        urls.forEach { url ->
            val parsed = url.toUrlWithContext(context)
            assertEquals(expectedUri, parsed)
            assertEquals(expectedStr, parsed.toString())
        }
    }

    @Test
    fun toUrl_preserves_parameters() {
        val href = "https://edition.cnn.com/2024/10/02/business/elon-musk-twitter-x-fidelity?cid=external-feeds_iluminar_google"
        val url = href.toUrl()
        assertEquals(href, url.toString())
    }

    @Test
    fun toCheckedUrl_removes_parameters() {
        val href = "https://edition.cnn.com/2024/10/02/business/elon-musk-twitter-x-fidelity?cid=external-feeds_iluminar_google"
        val expected = "https://edition.cnn.com/2024/10/02/business/elon-musk-twitter-x-fidelity"
        val url = href.toCheckedUrl(emptySet(), null)
        assertEquals(expected, url.toString())
    }
}