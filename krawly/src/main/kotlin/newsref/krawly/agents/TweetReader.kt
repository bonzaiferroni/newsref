package newsref.krawly.agents

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import newsref.db.models.PageInfo
import newsref.db.models.WebResult
import newsref.db.utils.jsonDecoder
import newsref.db.utils.stripParams
import newsref.db.utils.toNewDomain
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.contentToDoc
import newsref.krawly.utils.findFirstOrNull
import newsref.model.core.ArticleType
import newsref.model.core.SourceType
import newsref.model.core.Url
import newsref.model.core.toUrl
import newsref.model.data.Article
import newsref.model.data.FetchStrategy
import newsref.model.data.Host
import newsref.model.data.Source
import newsref.model.dto.PageAuthor
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class TweetReader(
	private val web: SpiderWeb,
) {
	fun read(url: Url, host: Host, result: WebResult?): PageInfo? {
		val now = Clock.System.now()
		if (result == null || !result.isOk || result.content == null) return null
		val tweetUrl = url.stripParams().toNewDomain("x.com")
		val embedInfo = jsonDecoder.decodeFromString<TweetEmbedInfo>(result.content!!)
		val html = embedInfo.html.decodeHtmlFromJson()
		val doc = html.contentToDoc()
		val content = doc.findFirstOrNull("blockquote>p")?.text
		val wordCount = content?.split(' ')?.size ?: 0
		val author = PageAuthor(name = embedInfo.authorName, url = embedInfo.authorUrl.decodeHtmlFromJson())
		val title = "${author.name} on twitter".let {
			if (content == null) return@let it
			val snippet = StringBuilder()
			val words = content.split(' ')
			for (word in words) {
				if (snippet.length + word.length > 50) {
					snippet.append("...")
					break
				}
				if (snippet.isEmpty())
					snippet.append(": “")
				else
					snippet.append(' ')
				snippet.append(word)
			}
			"$it$snippet”"
		}

		return PageInfo(
			source = Source(
				url = tweetUrl,
				title = title,
				type = SourceType.SOCIAL_POST,
				embed = html,
				accessedAt = now,
				seenAt = now,
			),
			pageHost = host,
			language = "en",
			contentWordCount = wordCount,
			foundNewsArticle = false,
			links = emptyList(),
			authors = listOf(author),
			hostName = "x.com",
			contents = setOf("$content\n\n(via twitter $now)"),
		)
	}
}

fun Url.toTweetEmbedUrl() = this.let {
	"https://publish.twitter.com/oembed?url=${it.href.encodeForUrl()}".toUrl()
}

val Url.isTweet get() = twitterCores.contains(this.core) && this.href.contains("/status/")

private val twitterCores = setOf("x.com", "twitter.com")

fun String.encodeForUrl(): String {
	return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}

fun String.decodeHtmlFromJson(): String {
	return this.replace(Regex("\\\\u([0-9A-Fa-f]{4})")) {
		val charCode = it.groupValues[1].toInt(16)
		charCode.toChar().toString()
	}
}

@Serializable
data class TweetEmbedInfo(
	val url: String,
	@SerialName("author_name")
	val authorName: String,
	@SerialName("author_url")
	val authorUrl: String,
	val html: String,
	val width: Int,
	val height: Int?,
	val type: String,
	@SerialName("cache_age")
	val cacheAge: String,
	@SerialName("provider_name")
	val providerName: String,
	@SerialName("provider_url")
	val providerUrl: String,
	val version: String
)

// {
//  "url": "https:\\/\\/twitter.com\\/Liz_Cheney\\/status\\/1852292100844621974",
//  "author_name": "Liz Cheney",
//  "author_url": "https:\\/\\/twitter.com\\/Liz_Cheney",
//  "html": "<blockquote class="twitter-tweet">
//<p lang="en" dir="ltr">This is how dictators destroy free nations. They threaten those who speak against them with death. We cannot entrust our country and our freedom to a petty, vindictive, cruel, unstable man who wants to be a tyrant. <a href="https://twitter.com/hashtag/Womenwillnotbesilenced?src=hash&amp;ref_src=twsrc%5Etfw">#Womenwillnotbesilenced</a> <a href="https://twitter.com/hashtag/VoteKamala?src=hash&amp;ref_src=twsrc%5Etfw">#VoteKamala</a> <a href="https://t.co/URH5s929Sa">https://t.co/URH5s929Sa</a></p>&mdash; Liz Cheney (@Liz_Cheney) <a href="https://twitter.com/Liz_Cheney/status/1852292100844621974?ref_src=twsrc%5Etfw">November 1, 2024</a>
//</blockquote>
//<script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>",
//  "width": 550,
//  "height": null,
//  "type": "rich",
//  "cache_age": "3153600000",
//  "provider_name": "Twitter",
//  "provider_url": "https:\\/\\/twitter.com",
//  "version": "1.0"
//}