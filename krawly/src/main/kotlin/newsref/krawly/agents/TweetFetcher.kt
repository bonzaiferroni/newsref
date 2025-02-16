package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.model.FetchInfo
import newsref.db.utils.stripParams
import newsref.db.utils.toNewDomain
import newsref.krawly.SpiderWeb
import newsref.model.core.CheckedUrl
import newsref.model.data.FetchStrategy
import newsref.model.data.Host
import newsref.model.data.LeadInfo
import newsref.model.data.LeadResult

private val console = globalConsole.getHandle("TweetFetcher")

class TweetFetcher(
	spindex: Int,
	private val web: SpiderWeb,
) {
	suspend fun fetch(lead: LeadInfo, leadUrl: CheckedUrl, leadHost: Host, pastResults: List<LeadResult>): FetchInfo? {
		if (!lead.url.isTweet) return null
		val tweetUrl = leadUrl.stripParams().toNewDomain("x.com")
		val result = web.fetch(tweetUrl.toTweetEmbedUrl(), FetchStrategy.BASIC)
		return FetchInfo(
			lead = lead,
			leadHost = leadHost,
			pastResults = pastResults,
			result = result,
			strategy = FetchStrategy.BASIC,
		)
	}
}