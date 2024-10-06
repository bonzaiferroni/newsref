package newsref.krawly.serializers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import newsref.krawly.models.Image
import newsref.krawly.models.QuantitativeValue

class MaybeListSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test NewsImageSerializer with single string input`() {
        val jsonString = "\"https://example.com/image.jpg\""
        val result = json.decodeFromString(ImageListSerializer, jsonString)

        val expected = listOf(Image(url = "https://example.com/image.jpg"))
        assertEquals(expected, result, "Expected a single image converted to a list of Image objects")
    }

    @Test
    fun `test NewsImageSerializer with list of strings input`() {
        val jsonString = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]"
        val result = json.decodeFromString(ImageListSerializer, jsonString)

        val expected = listOf(
            Image(url = "https://example.com/image1.jpg"),
            Image(url = "https://example.com/image2.jpg")
        )
        assertEquals(expected, result, "Expected a list of images created from the input URLs")
    }

    @Test
    fun `test QuantitativeValueSerializer with single Int input`() {
        val jsonString = "42"
        val result = json.decodeFromString(QuantitativeValueSerializer, jsonString)

        val expected = QuantitativeValue(value = 42)
        assertEquals(expected, result, "Expected a QuantitativeValue with value 42")
    }

    @Test
    fun `test QuantitativeValueSerializer with full object input`() {
        val jsonString = """
            {
                "value": 42,
                "unitCode": "cm"
            }
        """.trimIndent()
        val result = json.decodeFromString(QuantitativeValueSerializer, jsonString)

        val expected = QuantitativeValue(value = 42, unitCode = "cm")
        assertEquals(expected, result, "Expected a QuantitativeValue object with value 42 and unitCode 'cm'")
    }
}
