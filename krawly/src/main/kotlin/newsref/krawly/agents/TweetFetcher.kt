package newsref.krawly.agents

import kotlinx.serialization.Serializable
import newsref.db.globalConsole
import newsref.db.models.FetchInfo
import newsref.krawly.SpiderWeb
import newsref.model.core.CheckedUrl
import newsref.model.core.toCheckedUrl
import newsref.model.core.toUrl
import newsref.model.data.FetchStrategy
import newsref.model.data.Host
import newsref.model.data.LeadInfo
import newsref.model.data.LeadResult
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private val console = globalConsole.getHandle("TweetFetcher")

class TweetFetcher(
	spindex: Int,
	private val web: SpiderWeb,
) {
	suspend fun fetch(lead: LeadInfo, leadUrl: CheckedUrl, leadHost: Host, pastResults: List<LeadResult>): FetchInfo {
		val twitterUrl = leadUrl.href.toCheckedUrl(setOf(), setOf()).let {
			"https://publish.twitter.com/oembed?url=${it.href.encodeForUrl()}".toUrl()
		}
		console.log("twitter url:\n$twitterUrl")
		val result = web.fetch(twitterUrl, FetchStrategy.BASIC)
		return FetchInfo(
			lead = lead,
			leadHost = leadHost,
			pastResults = pastResults,
			result = result,
			strategy = FetchStrategy.BASIC,
		)
	}
}

fun String.encodeForUrl(): String {
	return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}

@Serializable
data class TweetEmbed(
	val url: String,
	val author_name: String,
	val author_url: String,
	val html: String,
	val width: Int,
	val height: Int?,
	val type: String,
	val cache_age: String,
	val provider_name: String,
	val provider_url: String,
	val version: String
)

// {
//  "url": "https:\\/\\/twitter.com\\/Liz_Cheney\\/status\\/1852292100844621974",
//  "author_name": "Liz Cheney",
//  "author_url": "https:\\/\\/twitter.com\\/Liz_Cheney",
//  "html": "\\u003Cblockquote class=\"twitter-tweet\"\\u003E\\u003Cp lang=\"en\" dir=\"ltr\"\\u003EThis is how dictators destroy free nations. They threaten those who speak against them with death. We cannot entrust our country and our freedom to a petty, vindictive, cruel, unstable man who wants to be a tyrant. \\u003Ca href=\"https:\\/\\/twitter.com\\/hashtag\\/Womenwillnotbesilenced?src=hash&amp;ref_src=twsrc%5Etfw\"\\u003E#Womenwillnotbesilenced\\u003C\\/a\\u003E \\u003Ca href=\"https:\\/\\/twitter.com\\/hashtag\\/VoteKamala?src=hash&amp;ref_src=twsrc%5Etfw\"\\u003E#VoteKamala\\u003C\\/a\\u003E \\u003Ca href=\"https:\\/\\/t.co\\/URH5s929Sa\"\\u003Ehttps:\\/\\/t.co\\/URH5s929Sa\\u003C\\/a\\u003E\\u003C\\/p\\u003E&mdash; Liz Cheney (@Liz_Cheney) \\u003Ca href=\"https:\\/\\/twitter.com\\/Liz_Cheney\\/status\\/1852292100844621974?ref_src=twsrc%5Etfw\"\\u003ENovember 1, 2024\\u003C\\/a\\u003E\\u003C\\/blockquote\\u003E\n\\u003Cscript async src=\"https:\\/\\/platform.twitter.com\\/widgets.js\" charset=\"utf-8\"\\u003E\\u003C\\/script\\u003E\n\n",
//  "width": 550,
//  "height": null,
//  "type": "rich",
//  "cache_age": "3153600000",
//  "provider_name": "Twitter",
//  "provider_url": "https:\\/\\/twitter.com",
//  "version": "1.0"
//}