package newsref.model.utils

import com.eygraber.uri.Uri
import com.eygraber.uri.Url
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlsTest {

    @Test
    fun remove_query_parameters_test() {
        val url = "http://example.com?param1=value1&param2=value2"
        val keepParams = setOf("param1")
        val expected = "http://example.com?param1=value1"
        assertEquals(expected, url.removeQueryParameters(keepParams))
    }

    @Test
    fun try_parse_relative_uri_test() {
        val urls = listOf(
            "/myPath",
            "myPath",
            "myPath?stripped=param"
        )
        val context = Url.parse("http://example.com")
        val expectedStr = "http://example.com/myPath"
        val expectedUri = Url.parse(expectedStr)
        urls.forEach { url ->
            val parsed = url.tryParseUntrustedUrl(emptySet(), context = context)
            assertEquals(expectedUri, parsed)
            assertEquals(expectedStr, parsed.toString())
        }
    }

    @Test
    fun try_parse_relative_url_test() {
        val urls = listOf(
            "/myPath",
            "myPath",
            "myPath?stripped=param"
        )
        val context = Url.parse("http://example.com")
        val expectedStr = "http://example.com/myPath"
        val expectedUri = Url.parse(expectedStr)
        urls.forEach { url ->
            val parsed = url.tryParseTrustedUrl(emptySet(), context = context)
            assertEquals(expectedUri.toString(), parsed.toString())
            assertEquals(expectedStr, parsed.toString())
        }
    }
}