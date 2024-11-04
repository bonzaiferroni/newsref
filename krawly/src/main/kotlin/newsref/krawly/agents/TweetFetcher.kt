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