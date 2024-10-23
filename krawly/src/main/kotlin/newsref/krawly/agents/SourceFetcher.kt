package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.models.FetchInfo
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.isFile
import newsref.model.core.toCheckedUrl
import newsref.model.data.*
import newsref.model.data.FetchStrategy.BASIC
import newsref.model.data.FetchStrategy.BROWSER
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class SourceFetcher(
	spindex: Int,
	private val web: SpiderWeb,
) {
	private val console = globalConsole.getHandle("$spindex SrcFetcher")

	suspend fun fetch(lead: LeadInfo, leadHost: Host, pastResults: List<LeadResult>): FetchInfo {
		if (lead.url.isFile() || decideSkipFetch(pastResults))
			return FetchInfo(lead = lead, leadHost = leadHost, pastResults = pastResults, skipFetch = true)

		val newParams = lead.url.params.map { it.key }.toSet() - leadHost.navParams
		var fetchUrl = newParams.takeIf { it.isNotEmpty() }
			?.let { lead.url.href.toCheckedUrl(newParams, leadHost.bannedPaths) }
			?: lead.url

		if (fetchUrl != lead.url) {
			// handle param experiments
			var navParams: Set<String>? = null
			var junkParams: Set<String>? = null
			var result = web.fetch(fetchUrl, BROWSER)
			if (result.isNotFound) {
				console.logDebug("result without params failed\n${lead.url}")
				delay(10.seconds)
				fetchUrl = lead.url
				result = web.fetch(fetchUrl, BROWSER)
				if (result.isOk) {
					navParams = newParams
					console.logDebug("found navParams: $navParams\n${lead.url}")
				}
			} else if (result.isOk) {
				junkParams = newParams
				console.logDebug("found junkParams: $junkParams\n${lead.url}")
			}
			return FetchInfo(
				lead = lead,
				leadHost = leadHost,
				pastResults = pastResults,
				result = result,
				strategy = BROWSER,
				navParams = navParams,
				junkParams = junkParams,
			)
		} else {
			// handle strategy swap
			var strategy = decideStrategy(pastResults)
			var failedStrategy: FetchStrategy? = null
			var result = web.fetch(lead.url, strategy)
			if (!result.isOk && pastResults.size < 10) {
				console.logDebug("$strategy strategy failed, swapping\n${lead.url}")
				delay(10.seconds)
				failedStrategy = strategy
				strategy = if (strategy == BASIC) BROWSER else BASIC
				result = web.fetch(lead.url, strategy)
			}
			return FetchInfo(
				lead = lead,
				leadHost = leadHost,
				pastResults = pastResults,
				result = result,
				strategy = strategy,
				failedStrategy = failedStrategy,
			)
		}
	}

	private fun decideSkipFetch(pastResults: List<LeadResult>): Boolean {
		val now = Clock.System.now()
		val resultMap = pastResults.filter { it.attemptedAt > now - 1.hours }.groupBy { it.result }
		val accessCount = resultMap.getTally(FetchResult.RELEVANT) + resultMap.getTally(FetchResult.IRRELEVANT)
		if (accessCount > 0) return false
		if (resultMap.getTally(FetchResult.CAPTCHA) > 0) return true
		return resultMap.getTally(FetchResult.TIMEOUT) > 5
	}

	private fun decideStrategy(pastResults: List<LeadResult>): FetchStrategy {
		val resultMap = pastResults.groupBy { it.strategy }
		val basicResults = resultMap[BASIC] ?: return BASIC
		// give basic a good try
		if (basicResults.size <= 3) return BASIC
		// catch news sites that can be basic crawled
		if (basicResults.any { it.result == FetchResult.RELEVANT }) return BASIC
		val browserResults = resultMap[BROWSER] ?: return BROWSER
		// catch news sites that must be browser crawled
		if (basicResults.all { !it.result.ok }) return BROWSER
		if (browserResults.any { it.result == FetchResult.RELEVANT }) return BROWSER
		return BASIC
	}
}