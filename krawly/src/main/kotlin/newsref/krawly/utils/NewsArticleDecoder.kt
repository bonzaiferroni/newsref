package newsref.krawly.utils

import kotlinx.serialization.json.*
import newsref.db.utils.jsonDecoder
import newsref.krawly.models.Image
import newsref.krawly.models.NewsAuthor
import newsref.krawly.models.NewsArticle
import newsref.krawly.models.Publisher

fun String.decodeNewsArticle(): NewsArticle? {
	var article = runCatching { jsonDecoder.decodeFromString<NewsArticle>(this) }.getOrNull()
	if (article != null) return article
	val array = runCatching { jsonDecoder.decodeFromString<JsonArray>(this) }.getOrNull()
	if (array != null) {
		for (element in array) {
			val jsonObject = element as? JsonObject ?: continue
			article = jsonObject.toNewsArticle()
			if (article != null) return article
		}
	}
	val obj = runCatching { jsonDecoder.decodeFromString<JsonObject>(this) }.getOrNull()
	return obj?.toNewsArticle()
}

fun JsonObject.toNewsArticle(): NewsArticle? {
	if (this["@type"]?.toString() != "\"NewsArticle\"") return null
	return NewsArticle(
		headline = this["headline"]?.toString(),
		dateline = this["dateline"]?.toString(),
		datePublished = this["datePublished"]?.toString(),
		dateModified = this["dateModified"]?.toString(),
		author = this["author"]?.toArray(JsonObject::toAuthor) { primitive -> NewsAuthor(name = primitive.toString()) },
		publisher = this["publisher"]?.toSingle(JsonObject::toPublisher) { primitive -> Publisher(name = primitive.toString()) },
	)
}

fun JsonObject.toPublisher() = Publisher(
	name = this["name"]?.toString(),
	logo = this["logo"]?.toSingle(JsonObject::toImage) { primitive -> Image(url = primitive.toString()) }
)

fun JsonObject.toAuthor() = NewsAuthor(
	name = this["name"]?.toString(),
	url = this["url"]?.toString(),
	email = this["email"]?.toString(),
	sameAs = this["sameAs"]?.toString(),
	image = this["image"]?.toSingle(JsonObject::toImage) { primitive -> Image(url = primitive.toString()) }
)

fun JsonObject.toImage() = Image(
	width = this["width"]?.toSingle(JsonObject::toQuantInt) { primitive -> primitive.intOrNull },
	height = this["height"]?.toSingle(JsonObject::toQuantInt) { primitive -> primitive.intOrNull },
	url = this["url"]?.toString(),
	caption = this["caption"]?.toString(),
	creditText = this["creditText"]?.toString()
)

fun JsonObject.toQuantInt() = this["value"]?.jsonPrimitive?.intOrNull

fun JsonElement.getImage(): Image? {
	return when (this) {
		is JsonObject -> Image(
			width = this["width"]?.getQuantitativeValueInt(),
			height = this["height"]?.getQuantitativeValueInt(),
			url = this["url"]?.toString()
		)

		is JsonArray -> this.firstOrNull()?.getImage()
		is JsonPrimitive -> Image(url = this.toString())
		else -> null
	}
}


fun JsonElement.getAuthor(): NewsAuthor? {
	return when (this) {
		is JsonObject -> NewsAuthor(
			name = this.getStringValue("name"),
			url = this.getStringValue("url"),
			email = this.getStringValue("email"),
			sameAs = this.getStringValue("sameAs"),
			image = this["image"]?.getImage()
		)

		is JsonPrimitive -> NewsAuthor(name = this.toString())
		is JsonArray -> this.firstOrNull()?.getAuthor()
		else -> null
	}
}

fun JsonElement.getPublisher(): Publisher? {
	return when (this) {
		is JsonObject -> Publisher(
			name = this.getStringValue("name"),
			logo = this["logo"]?.getImages()
		)

		is JsonPrimitive -> Publisher(name = this.toString())
		else -> null
	}
}

fun JsonElement.getAuthors(): List<NewsAuthor>? {
	return when (this) {
		is JsonObject -> this.getAuthor()?.let { listOf(it) }
		is JsonPrimitive -> this.getAuthor()?.let { listOf(it) }
		is JsonArray -> this.mapNotNull { it.getAuthor() }
		else -> null
	}
}

fun <T> JsonElement.toArray(
	convertObject: (JsonObject) -> T,
	convertPrimitive: (JsonPrimitive) -> T
): List<T>? {
	return when (this) {
		is JsonObject -> listOf(convertObject(this)) //this.getAuthor()?.let { listOf(it) }
		is JsonPrimitive -> listOf(convertPrimitive(this))
		is JsonArray -> this.mapNotNull {
			when (it) {
				is JsonObject -> convertObject(it)
				is JsonPrimitive -> convertPrimitive(it)
				else -> null
			}
		}

		else -> null
	}
}

fun <T> JsonElement.toSingle(
	convertObject: (JsonObject) -> T,
	convertPrimitive: (JsonPrimitive) -> T
): T? {
	return when (this) {
		is JsonObject -> convertObject(this)
		is JsonPrimitive -> convertPrimitive(this)
		is JsonArray -> this.firstOrNull()?.let {
			when (it) {
				is JsonObject -> convertObject(it)
				is JsonPrimitive -> convertPrimitive(it)
				else -> null
			}
		}

		else -> null
	}
}

fun JsonElement.getImages(): List<Image>? {
	return when (this) {
		is JsonObject -> this.toImage()?.let { listOf(it) }
		is JsonArray -> this.mapNotNull { it.getImage() }
		is JsonPrimitive -> listOf(Image(url = this.toString()))
		else -> null
	}
}

fun JsonElement.getQuantitativeValueInt(): Int? {
	return when (this) {
		is JsonObject -> this["value"]?.jsonPrimitive?.intOrNull
		is JsonPrimitive -> this.intOrNull
		else -> null
	}
}

fun JsonObject.getStringValue(propertyName: String): String? = this[propertyName]?.toString()
