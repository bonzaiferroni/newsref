package newsref.db.utils

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import newsref.db.models.NewsArticle

val prettyPrintJson = Json { prettyPrint = true; ignoreUnknownKeys = true }

fun String.readArrayOrObject(): NewsArticle? {
	try {
		// Try to decode the string as a JsonArray first
		val jsonArray = prettyPrintJson.decodeFromString<JsonArray>(this)
		return jsonArray.firstNotNullOfOrNull { jsonElement ->
			val jsonObject = jsonElement as? JsonObject ?: return@firstNotNullOfOrNull null
			val type = jsonObject["@type"].toString()
			if (type != "\"NewsArticle\"") return@firstNotNullOfOrNull null
			runCatching { prettyPrintJson.decodeFromJsonElement<NewsArticle>(jsonObject) }
				.getOrNull()
		} ?: throw SerializationException("No valid NewsArticle found in JSON array")
	} catch (e: SerializationException) {
		return try {
			// If it's not a JsonArray, try to decode it directly as a NewsArticle
			prettyPrintJson.decodeFromString<NewsArticle>(this)
		} catch (e: Exception) {
			null
		}
	}
}