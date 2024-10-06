package newsref.krawly.utils

import kotlinx.serialization.json.*
import newsref.db.utils.jsonDecoder
import newsref.krawly.models.Image
import newsref.krawly.models.NewsAuthor
import newsref.krawly.models.NewsArticle
import newsref.krawly.models.Publisher

fun String.decodeNewsArticle(): NewsArticle? {
	val element = runCatching { jsonDecoder.decodeFromString<JsonElement>(this) }.getOrNull() ?: return null
	when (element) {
		is JsonObject -> {
			val article = element.toNewsArticle()
			if (article != null) return article
			val graph = element["@graph"] as? JsonArray ?: return null
			return graph.findType("NewsArticle", JsonObject::toNewsArticle)
		}
		is JsonArray -> {
			return element.findType("NewsArticle", JsonObject::toNewsArticle)
		}
		else -> return null
	}
}

fun JsonObject.toNewsArticle(): NewsArticle? {
	if (this["@type"]?.toString()?.contains("NewsArticle") != true) return null
	return NewsArticle(
		headline = this["headline"]?.jsonPrimitive?.contentOrNull,
		dateline = this["dateline"]?.jsonPrimitive?.contentOrNull,
		datePublished = this["datePublished"]?.jsonPrimitive?.contentOrNull,
		dateModified = this["dateModified"]?.jsonPrimitive?.contentOrNull,
		author = this["author"]?.toArray(JsonObject::toAuthor) { NewsAuthor(name = it.toString()) },
		publisher = this["publisher"]?.toSingle(JsonObject::toPublisher) { Publisher(name = it.toString()) },
		articleBody = this["articleBody"]?.jsonPrimitive?.contentOrNull,
		articleSection = this["articleSection"]?.toStrings(),
		wordCount = this["wordCount"]?.jsonPrimitive?.intOrNull,
		keywords = this["keywords"]?.toStrings(),
		abstract = this["abstract"]?.jsonPrimitive?.contentOrNull,
		alternativeHeadline = this["alternativeHeadline"]?.jsonPrimitive?.contentOrNull,
		description = this["description"]?.jsonPrimitive?.contentOrNull,
		url = this["url"]?.jsonPrimitive?.contentOrNull,
		isAccessibleForFree = this["isAccessibleForFree"]?.jsonPrimitive?.booleanOrNull,
		text = this["text"]?.jsonPrimitive?.contentOrNull,
		thumbnailUrl = this["thumbnailUrl"]?.jsonPrimitive?.contentOrNull,
		image = this["image"]?.toArray(JsonObject::toImage) { Image(url = it.toString()) },
		inLanguage = this["inLanguage"]?.jsonPrimitive?.contentOrNull,
		commentCount = this["commentCount"]?.jsonPrimitive?.intOrNull
	)
}

fun JsonObject.toPublisher() = Publisher(
	name = this["name"]?.jsonPrimitive?.contentOrNull,
	logo = this["logo"]?.toSingle(JsonObject::toImage) { Image(url = it.toString()) }
)

fun JsonObject.toAuthor() = NewsAuthor(
	name = this["name"]?.jsonPrimitive?.contentOrNull,
	url = this["url"]?.jsonPrimitive?.contentOrNull,
	email = this["email"]?.jsonPrimitive?.contentOrNull,
	sameAs = this["sameAs"]?.toStrings(),
	image = this["image"]?.toSingle(JsonObject::toImage) { Image(url = it.toString()) }
)

fun JsonObject.toImage() = Image(
	width = this["width"]?.toSingle(JsonObject::toQuantInt) { it.intOrNull },
	height = this["height"]?.toSingle(JsonObject::toQuantInt) { it.intOrNull },
	url = this["url"]?.jsonPrimitive?.contentOrNull,
	caption = this["caption"]?.jsonPrimitive?.contentOrNull,
	creditText = this["creditText"]?.jsonPrimitive?.contentOrNull,
)

fun JsonObject.toQuantInt() = this["value"]?.jsonPrimitive?.intOrNull


