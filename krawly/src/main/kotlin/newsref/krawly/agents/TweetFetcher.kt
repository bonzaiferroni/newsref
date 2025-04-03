package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.model.FetchInfo
import newsref.db.utils.stripParams
import newsref.db.utils.toNewDomain
import newsref.krawly.SpiderWeb
import newsref.db.core.CheckedUrl
import newsref.db.core.LogBook
import newsref.db.model.FetchStrategy
import newsref.db.model.Host
import newsref.db.model.LeadInfo
import newsref.db.model.LeadResult

private val console = globalConsole.getHandle("TweetFetcher")

class TweetFetcher(
	spindex: Int,
	private val web: SpiderWeb,
) {
	suspend fun fetch(lead: LeadInfo, leadUrl: CheckedUrl, leadHost: Host, pastResults: List<LeadResult>): FetchInfo? {
		val logBook = LogBook()
		if (!lead.url.isTweet) return null
		val tweetUrl = leadUrl.stripParams().toNewDomain("x.com")
		val result = web.fetch(tweetUrl.toTweetEmbedUrl(), FetchStrategy.BASIC)
		return FetchInfo(
			lead = lead,
			leadHost = leadHost,
			pastResults = pastResults,
			result = result,
			strategy = FetchStrategy.BASIC,
			logBook = logBook,
		)
	}
}