package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.models.FetchInfo
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.contentToDoc
import newsref.krawly.utils.isFile
import newsref.model.core.*
import newsref.model.data.*
import newsref.model.data.FetchStrategy.BASIC
import newsref.model.data.FetchStrategy.BROWSER
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class SourceFetcher(
	spindex: Int,
	private val web: SpiderWeb,
	private val tweetFetcher: TweetFetcher = TweetFetcher(spindex, web)
) {
	private val console = globalConsole.getHandle("$spindex SrcFetcher")

	private val specialFetchers = mapOf(
		"x.com" to tweetFetcher,
		"twitter.com" to tweetFetcher
	)

	suspend fun fetch(lead: LeadInfo, leadUrl: CheckedUrl, leadHost: Host, pastResults: List<LeadResult>): FetchInfo {
		val specialFetch = specialFetchers[lead.url.core]?.fetch(lead, leadUrl, leadHost, pastResults)
		if (specialFetch != null) return specialFetch

		if (decideSkipFetch(lead, pastResults))
			return FetchInfo(lead = lead, leadHost = leadHost, pastResults = pastResults, skipFetch = true)

		val newParams = leadUrl.params.map { it.key }.toSet() - leadHost.navParams
		val firstParam = newParams.takeIf { it.isNotEmpty() }?.let { setOf(it.first()) } ?: emptySet()
		val strippedUrl = firstParam.takeIf { it.isNotEmpty() }
			?.let { leadUrl.href.toCheckedUrl(firstParam, leadHost.bannedPaths) }
			?: leadUrl

		if (strippedUrl != leadUrl) {
			// handle param experiments
			val originalResult = web.fetch(leadUrl, BROWSER)
			val resultPageUrl = originalResult.pageHref?.toUrlOrNull()
			val originalTitle = originalResult.content?.contentToDoc()?.titleText
			if (!originalResult.isOk || originalTitle == null
				|| resultPageUrl == null || resultPageUrl.core != leadUrl.core) {
				console.logTrace("safe params: ${leadUrl.core} $newParams")
				return FetchInfo(
					lead = lead,
					leadHost = leadHost,
					pastResults = pastResults,
					strategy = BROWSER,
					result = originalResult,
					navParams = newParams
				)
			}
			delay(60.seconds)
			val newResult = web.fetch(strippedUrl, BROWSER)
			val newTitle = newResult.content?.contentToDoc()?.titleText

			if (!newResult.isOk || newTitle != originalTitle) {
				console.logTrace("nav Param ${lead.url.core} $firstParam")
				return FetchInfo(
					lead = lead,
					leadHost = leadHost,
					pastResults = pastResults,
					result = originalResult,
					strategy = BROWSER,
					navParams = firstParam,
				)
			}
			console.logTrace("junk param ${lead.url.core} $firstParam")
			return FetchInfo(
				lead = lead,
				leadHost = leadHost,
				pastResults = pastResults,
				result = newResult,
				strategy = BROWSER,
				junkParams = firstParam,
			)
		} else {
			var strategy = decideStrategy(pastResults)
			var failedStrategy: FetchStrategy? = null
			var result = web.fetch(leadUrl, strategy)
			if (!result.isOk && pastResults.size < 10) {
				console.logTrace("$strategy strategy failed, swapping\n${leadUrl}")
				delay(30.seconds)
				failedStrategy = strategy
				strategy = if (strategy == BASIC) BROWSER else BASIC
				result = web.fetch(leadUrl, strategy)
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

	private fun decideSkipFetch(lead: LeadInfo, pastResults: List<LeadResult>): Boolean {
		if (lead.url.isFile()) return true
		if (skipHosts.contains(lead.url.core)) return true
		val now = Clock.System.now()
		val resultMap = pastResults.filter { it.attemptedAt > now - 1.hours }.groupBy { it.result }
		if (resultMap.getTally(FetchResult.CAPTCHA) > 0) return true
		val accessCount = resultMap.getTally(FetchResult.RELEVANT) + resultMap.getTally(FetchResult.IRRELEVANT)
		if (accessCount > 0) return false
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

val safeParams = setOf("v", "id", "index", "releaseid")
val skipHosts = setOf("facebook.com")