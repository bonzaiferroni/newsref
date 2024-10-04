package newsref.krawly.agents

import kotlinx.coroutines.*
import newsref.db.services.FeedService
import newsref.db.services.LeadJobService
import newsref.db.services.LeadService
import newsref.krawly.SpiderWeb
import newsref.krawly.log.LogConsole
import newsref.krawly.log.underline
import newsref.krawly.utils.isMaybeRelevant
import newsref.krawly.utils.tryGetHref
import newsref.model.core.toCheckedUrl
import newsref.model.core.toUrlOrNull
import newsref.model.data.LeadJob
import kotlin.time.Duration.Companion.minutes

class FeedChecker(
	console: LogConsole,
	private val web: SpiderWeb,
	private val outletAgent: OutletAgent,
	private val feedService: FeedService = FeedService(),
	private val leadJobService: LeadJobService = LeadJobService(),
) {
	private val console = console.getHandle("FeedChecker")

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			feedService.init()                                                  //    FeedService ->
			while (true) {
				console.log("checking feeds", "🕷  ")
				checkFeeds()
				console.log("sleeping", "zzz")
				delay(15.minutes)
			}
		}
	}

	private suspend fun checkFeeds() {
		val feeds = feedService.readAll()                                       // <- FeedService
		val jobs = mutableListOf<LeadJob>()
		for (feed in feeds) {
			console.log("checking feed: ${feed.url}")
			val webResult = web.crawlPage(feed.url)                             // <- Web
			if (webResult == null || !webResult.isSuccess() || webResult.doc == null) {
				console.logError("feed error: ${feed.url}")
				continue
			}
			val doc = webResult.doc                                             // <- Parse
			val elements = doc.findAll(feed.selector)
			console.log("found ${elements.size} elements at ${
				feed.url.host.underline()} with selector ${feed.selector.underline()}")
			for (docElement in doc.findAll(feed.selector)) {
				val (headline, href) = docElement.tryGetHref() ?: continue
				val url = href.toUrlOrNull() ?: continue
				if (!url.isMaybeRelevant()) continue
				val outlet = outletAgent.getOutlet(url)                         // <- OutletAgent ->
				val checkedUrl = href.toCheckedUrl(outlet)
				jobs += LeadJob(feedId = feed.id, url = checkedUrl, headline = headline)
			}
			console.log("found ${jobs.size} feed leads from ${elements.size} elements")
		}

		var count = 0
		for (job in jobs) {
			try {
				leadJobService.createIfFreshLead(job)
				count++
			} catch (e: IllegalArgumentException) {
				console.logDebug(e.message ?: "Error creating job: ${job.url}")
			}
		}
		console.log("created $count new leadJobs")
	}
}