package newsref.krawly.utils

import kotlinx.serialization.json.*
import newsref.db.utils.jsonDecoder
import newsref.krawly.models.Image
import newsref.krawly.models.NewsArticle
import newsref.krawly.models.NewsAuthor
import newsref.krawly.models.Publisher

fun String.decodeNewsArticle(): NewsArticle? {
	val element = runCatching { jsonDecoder.decodeFromString<JsonElement>(this) }.getOrNull() ?: return null
	when (element) {
		is JsonObject -> {
			val article = element.toNewsArticle()
			if (article != null) return article
			element["@graph"]?.let {
				when (it) {
					is JsonObject -> return it.toNewsArticle()
					is JsonArray -> return it.findType("NewsArticle", JsonObject::toNewsArticle)
					else -> return null
				}
			}
			return null
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
		headline = this["headline"]?.primitiveOrNull?.contentOrNull,
		dateline = this["dateline"]?.primitiveOrNull?.contentOrNull,
		datePublished = this["datePublished"]?.primitiveOrNull?.contentOrNull,
		dateModified = this["dateModified"]?.primitiveOrNull?.contentOrNull,
		author = this["author"]?.toArray(JsonObject::toAuthor) { NewsAuthor(name = it.toString()) },
		publisher = this["publisher"]?.toSingle(JsonObject::toPublisher) { Publisher(name = it.toString()) },
		articleBody = this["articleBody"]?.primitiveOrNull?.contentOrNull,
		articleSection = this["articleSection"]?.toStrings(),
		wordCount = this["wordCount"]?.primitiveOrNull?.intOrNull,
		keywords = this["keywords"]?.toStrings(),
		abstract = this["abstract"]?.primitiveOrNull?.contentOrNull,
		alternativeHeadline = this["alternativeHeadline"]?.primitiveOrNull?.contentOrNull,
		description = this["description"]?.primitiveOrNull?.contentOrNull,
		url = this["url"]?.primitiveOrNull?.contentOrNull,
		isAccessibleForFree = this["isAccessibleForFree"]?.primitiveOrNull?.booleanOrNull,
		text = this["text"]?.primitiveOrNull?.contentOrNull,
		thumbnailUrl = this["thumbnailUrl"]?.primitiveOrNull?.contentOrNull,
		image = this["image"]?.toArray(JsonObject::toImage) { Image(url = it.toString()) },
		inLanguage = this["inLanguage"]?.primitiveOrNull?.contentOrNull,
		commentCount = this["commentCount"]?.primitiveOrNull?.intOrNull
	)
}

fun JsonObject.toPublisher() = Publisher(
	name = this["name"]?.primitiveOrNull?.contentOrNull,
	logo = this["logo"]?.toSingle(JsonObject::toImage) { Image(url = it.toString()) }
)

fun JsonObject.toAuthor() = NewsAuthor(
	name = this["name"]?.primitiveOrNull?.contentOrNull,
	url = this["url"]?.primitiveOrNull?.contentOrNull,
	email = this["email"]?.primitiveOrNull?.contentOrNull,
	sameAs = this["sameAs"]?.toStrings(),
	image = this["image"]?.toSingle(JsonObject::toImage) { Image(url = it.toString()) }
)

fun JsonObject.toImage() = Image(
	width = this["width"]?.toSingle(JsonObject::toQuantInt) { it.intOrNull },
	height = this["height"]?.toSingle(JsonObject::toQuantInt) { it.intOrNull },
	url = this["url"]?.primitiveOrNull?.contentOrNull,
	caption = this["caption"]?.primitiveOrNull?.contentOrNull,
	creditText = this["creditText"]?.primitiveOrNull?.contentOrNull,
)

fun JsonObject.toQuantInt() = this["value"]?.primitiveOrNull?.intOrNull


