package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.core.Url
import newsref.db.core.toUrl
import newsref.db.core.toUrlOrNull
import newsref.db.globalConsole
import newsref.db.utils.cacheResource
import newsref.db.model.CrawlInfo
import newsref.db.model.FetchInfo
import newsref.db.model.FetchResult
import newsref.db.model.LeadResult
import newsref.db.model.CrawledData
import newsref.db.model.WebResult
import newsref.krawly.utils.toMarkdown
import newsref.model.data.ContentType

class PageReader(
	private val hostAgent: HostAgent,
	private val pageParser: PageParser = PageParser(hostAgent),
	private val tweetReader: TweetReader = TweetReader()
) {
	private val console = globalConsole.getHandle("SourceReader")

	suspend fun read(fetch: FetchInfo): CrawlInfo {
		val (pageHost, pageUrl) = fetch.result?.takeIf { it.isOk }?.pageHref?.toUrlOrNull()
			?.let { hostAgent.getHost(it) } ?: Pair(null, null)
		val crawledData = fetch.lead.url.takeIf { it.isTweet }?.let {
			val (twitterHost, _) = hostAgent.getHost("https://x.com/".toUrl())
			tweetReader.read(fetch.lead, it, twitterHost, fetch.result)
		} ?: fetch.result?.takeIf { it.isOk && it.pageHref != null }?.let {
			pageParser.read(fetch.lead, it, pageUrl, pageHost, fetch.logBook)
		}
		val resultType = determineResultType(fetch.skipFetch, fetch.result, crawledData)
		if (resultType != FetchResult.TIMEOUT && !fetch.skipFetch)
			cacheResult(fetch.result, fetch.lead.url)

		val junkParams = crawledData?.page?.cannonUrl?.toUrlOrNull()?.takeIf { fetch.lead.url.core == it.core }
			?.let {
				val leadParams = fetch.lead.url.params.keys.toSet()
				val cannonParams = it.params.keys.toSet()
				leadParams - cannonParams
			}?.takeIf { it.isNotEmpty() }

		junkParams?.let { console.logTrace("cannon junkParams ${pageHost?.core}: $junkParams") }

		// parse strategies
		val crawl = CrawlInfo(
			crawledData = crawledData,
			fetchResult = resultType,
			fetch = fetch.copy(
				pastResults = fetch.pastResults.toMutableList().also {
					it.add(
                        LeadResult(result = resultType, attemptedAt = Clock.System.now(), strategy = fetch.strategy)
					)
				}
			),
			cannonJunkParams = junkParams
		)
		// todo: if it is a news article, save a video of the endpoint

		if (crawledData != null) {
			val md = crawl.toMarkdown()
			md?.cacheResource(crawledData.page.url.core, "md")
		}

		return crawl
	}

	private fun determineResultType(skipFetch: Boolean, result: WebResult?, crawledData: CrawledData?): FetchResult {
		if (skipFetch) return FetchResult.SKIPPED
		if (result == null) return FetchResult.UNKNOWN
		if (result.timeout) return FetchResult.TIMEOUT
		if (result.status in 400..499) return FetchResult.UNAUTHORIZED
		if (result.exception != null) return FetchResult.ERROR
		if (crawledData == null) return FetchResult.UNKNOWN
		// todo: better bot detection
		val title = crawledData.page.title
		if (title != null && title.contains("you") && (title.contains("robot") || title.contains("human"))
			&& title.endsWith('?')
		)
			return FetchResult.CAPTCHA
		// todo: support other languages
		if (crawledData.language?.startsWith("en") != true) return FetchResult.IRRELEVANT
		if (crawledData.page.contentType == ContentType.NewsArticle) {
			if (crawledData.foundNewsArticle) return FetchResult.RELEVANT
			val wordCount = crawledData.page.wordCount ?: 0
			val maybeUseful = crawledData.page.publishedAt != null && crawledData.links.any { it.isExternal } && wordCount > 100
			if (maybeUseful) return FetchResult.RELEVANT
			// todo: add more relevance indicators
		}
		return FetchResult.IRRELEVANT
	}

	private fun cacheResult(result: WebResult?, url: Url) {
//        result?.screenshot?.cacheResource(url.domain, "png")
		result?.content?.cacheResource(url.core, "html", "html")
	}
}

fun Map<FetchResult, Int>.getResult(fetchResult: FetchResult) = this[fetchResult] ?: 0
fun <T, V> Map<T, List<V>>.getTally(key: T) = this[key]?.size ?: 0
fun <T, V> Map<T, List<V>>.getTally(key: T, tallyIf: (V) -> Boolean) = this[key]?.count(tallyIf) ?: 0
fun <T, V> Map<T, List<V>>.getSum(key: T, sumIf: (V) -> Int) = this[key]?.sumOf(sumIf) ?: 0

val FetchResult.ok
	get() = when (this) {
		FetchResult.UNKNOWN -> false
		FetchResult.ERROR -> false
		FetchResult.SKIPPED -> false
		FetchResult.TIMEOUT -> false
		FetchResult.UNAUTHORIZED -> false
		FetchResult.CAPTCHA -> false
		FetchResult.IRRELEVANT -> true
		FetchResult.RELEVANT -> true
	}

