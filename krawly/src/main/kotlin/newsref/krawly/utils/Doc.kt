package newsref.krawly.utils

import com.eygraber.uri.Url
import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import newsref.db.models.NewsArticle
import newsref.db.serializers.globalJson
import newsref.db.utils.cacheResource
import newsref.db.utils.tryParse
import newsref.db.utils.tryParseInstantOrNull
import newsref.model.data.toSourceType

fun Doc.readMetaContent(vararg propertyValues: String) = propertyValues.firstNotNullOfOrNull {
    var value = this.findFirstOrNull("meta[property=\"$it\"]")?.attributes?.get("content")
    if (value == null)
        value = this.findFirstOrNull("meta[name=\"$it\"]")?.attributes?.get("content")
    value // return
}

fun Doc.readUrl() = this.readMetaContent("url", "og:url", "twitter:url")
fun Doc.readHeadline() = this.readMetaContent("title", "og:title", "twitter:title")
fun Doc.readDescription() = this.readMetaContent("description", "og:description", "twitter:description")
fun Doc.readImageUrl() = this.readMetaContent("image", "og:image", "twitter:image")
fun Doc.readOutletName() = this.readMetaContent("site", "og:site_name", "twitter:site")
fun Doc.readType() = this.readMetaContent("type", "og:type")?.toSourceType()
fun Doc.readAuthor() = this.readMetaContent("author", "article:author", "og:article:author")
fun Doc.readPublishedAt() = this.readMetaContent("date", "article:published_time")?.tryParseInstantOrNull()
fun Doc.readModifiedAt() = this.readMetaContent("last-modified", "article:modified_time")?.tryParseInstantOrNull()

fun Doc.findFirstOrNull(cssSelector: String): DocElement? = try {
    this.findFirst(cssSelector)
} catch (e: ElementNotFoundException) {
    null
}

// NewsArticle parsing
fun Doc.getNewsArticle(cacheId: Url): NewsArticle? {
    val innerHtml = this.findFirstOrNull("script#json-schema")?.html ?: this.scanTagsForNewsArticle() ?: return null
    val json = innerHtml.trimCData()
    json.cacheResource(cacheId, "json", "news_article_raw")
    val article = json.readArrayOrObject()
    if (article == null)
        json.cacheResource(cacheId, "json", "news_article_parsed")
    return article // return
}

private fun String.trimCData() = this
    .removePrefix("//<![CDATA[")
    .removeSuffix("//]]>")
    .trim()

private fun Doc.scanTagsForNewsArticle(): String? {
    for (element in this.findFirst("head").children) {
        if (element.tagName == "script" && element.html.contains("NewsArticle")) {
            return element.html
        }
    }
    return null
}

private fun String.readArrayOrObject(): NewsArticle? {
    try {
        // Try to decode the string as a JsonArray first
        val jsonArray = globalJson.decodeFromString<JsonArray>(this)
        return jsonArray.firstNotNullOfOrNull { jsonElement ->
            val jsonObject = jsonElement as? JsonObject ?: return@firstNotNullOfOrNull null
            val type = jsonObject["@type"].toString()
            if (type != "\"NewsArticle\"") return@firstNotNullOfOrNull null
            runCatching { globalJson.decodeFromJsonElement<NewsArticle>(jsonObject) }
                .getOrNull()
        } ?: throw SerializationException("No valid NewsArticle found in JSON array")
    } catch (e: SerializationException) {
        return try {
            // If it's not a JsonArray, try to decode it directly as a NewsArticle
            globalJson.decodeFromString<NewsArticle>(this)
        } catch (e: Exception) {
            null
        }
    }
}