package model

import newsref.model.core.toUrl
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantsTest {

    @Test
    fun test_uri_reversibility() {
        val strings = listOf(
            "http://example.com",
            "https://example.com",
            "/path-only",
            "https://example.com/with-path",
            "https://example.com/with-path#and-fragment"
        )
        val uris = strings.map { it.toUrl() }
        strings.forEachIndexed { index, s ->
            assertEquals(s, uris[index].toString())
        }
    }
}