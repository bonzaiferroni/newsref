package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.log.Justify
import newsref.db.log.darkPlumBg
import newsref.db.services.FeedService
import newsref.krawly.SpiderWeb
import newsref.db.log.underline
import newsref.db.services.CreateLeadResult
import newsref.krawly.utils.contentToDoc
import newsref.krawly.utils.isLikelyAd
import newsref.krawly.utils.tryGetHrefOrParent
import newsref.model.core.toUrlWithContextOrNull
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
			while (true) {
				console.log("checking feeds", "🕷 ")
				checkFeeds()
				console.log("sleeping", "zzz")
				delay(10.minutes)
			}
		}
	}

	private suspend fun checkFeeds() {
		val feeds = feedService.readAll().shuffled()
		val now = Clock.System.now()
		data class ConsoleMessage(val core: String, val count: Int, val links: Int, val elements: Int)
		val messages = mutableListOf<ConsoleMessage>()
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

			val elements = try {
				doc.findAll(feed.selector)
			} catch (e: Exception) {
				console.logWarning("no leads with selector: ${feed.selector}\n${feed.url}")
				emptyList()
			}
			for (docElement in elements) {
				val (headline, href) = docElement.tryGetHrefOrParent() ?: continue
				val url = href.toUrlWithContextOrNull(feed.url) ?: continue
				if (url.isLikelyAd()) continue
				if (feed.external && url.core == feed.url.core) continue
				if (!feed.external && url.core != feed.url.core) continue
				val (host, hostUrl) = hostAgent.getHost(url)
				val job = LeadJob(
					feedId = feed.id,
					headline = headline,
					isExternal = true,
					freshAt = Clock.System.now()
				)
				links++
				// todo: track FeedPosition
				val result = leadMaker.makeLead(hostUrl, job, true)
				if (result == CreateLeadResult.CREATED) count++
			}
			messages.add(ConsoleMessage(feed.url.core, count, links, elements.size))
			if (count == 0) delayMap[feed.id] = now + 1.hours
		}
		messages.forEach {
			console.cell(it.core, 20, "core", Justify.LEFT).cell(it.count, 5, "count").cell(it.links, 5, "links")
				.cell(it.elements, 5, "elements").row(background = darkPlumBg)
		}
	}
}