package newsref.model

import com.eygraber.uri.Uri
import com.eygraber.uri.Url
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
        val uris = strings.map { Uri.parse(it)}
        strings.forEachIndexed { index, s ->
            assertEquals(s, uris[index].toString())
        }
    }
}