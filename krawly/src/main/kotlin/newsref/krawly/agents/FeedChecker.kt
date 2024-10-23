package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.services.FeedService
import newsref.krawly.SpiderWeb
import newsref.db.log.underline
import newsref.krawly.utils.contentToDoc
import newsref.krawly.utils.isLikelyAd
import newsref.krawly.utils.tryGetHrefOrParent
import newsref.model.core.toUrlOrNull
import newsref.model.data.LeadJob
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class FeedChecker(
	private val web: SpiderWeb,
	private val hostAgent: HostAgent,
	private val leadMaker: LeadMaker,
	private val feedService: FeedService = FeedService(),
) {
	private val console = globalConsole.getHandle("FeedChecker")
	private val delayMap: MutableMap<Int, Instant> = mutableMapOf()

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			feedService.init()
			while (true) {
				console.log("checking feeds", "🕷 ")
				checkFeeds()
				console.log("sleeping", "zzz")
				delay(10.minutes)
			}
		}
	}

	private suspend fun checkFeeds() {
		val feeds = feedService.readAll()
		val now = Clock.System.now()
		for (feed in feeds) {
			val delay = delayMap[feed.id]
			if (delay != null && delay > now) continue

			var count = 0
			var links = 0
			console.logTrace("checking feed: ${feed.url} with selector ${feed.selector.underline()}")
			val webResult = web.fetch(feed.url)
			val content = webResult.content
			if (content == null) {
				console.logError("feed error: ${feed.url}")
				continue
			}
			val doc = content.contentToDoc()
			val elements = doc.findAll(feed.selector)
			for (docElement in doc.findAll(feed.selector)) {
				val (headline, href) = docElement.tryGetHrefOrParent() ?: continue
				val url = href.toUrlOrNull() ?: continue
				if (url.isLikelyAd()) continue
				val (host, hostUrl) = hostAgent.getHost(url)
				val job = LeadJob(
					feedId = feed.id,
					headline = headline,
					isExternal = true,
					freshAt = Clock.System.now()
				)
				links++
				val newJob = leadMaker.makeLead(hostUrl, host, job)
				if (newJob == CreateLeadResult.CREATED) count++
			}
			console.logInfo("${feed.url.domain.underline()}: $count leads from $links links / ${elements.size} elements")
			if (count == 0) delayMap[feed.id] = now + 1.hours
		}
	}
}