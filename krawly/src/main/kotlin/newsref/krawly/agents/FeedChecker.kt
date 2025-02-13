package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.console.Justify
import newsref.db.console.darkPlumBg
import newsref.db.services.FeedService
import newsref.krawly.SpiderWeb
import newsref.db.console.underline
import newsref.db.services.CreateLeadResult
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.krawly.utils.isLikelyAd
import newsref.krawly.utils.tryGetHrefOrChild
import newsref.krawly.utils.tryGetHrefOrParent
import newsref.model.core.SourceType
import newsref.model.core.toUrlWithContextOrNull
import newsref.model.data.FeedSource
import newsref.model.data.LeadJob
import kotlin.math.pow
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("FeedChecker")

class FeedChecker(
    private val web: SpiderWeb,
    private val hostAgent: HostAgent,
    private val leadMaker: LeadMaker,
    private val feedService: FeedService = FeedService(),
    private val sourceService: SourceService = SourceService(),
    private val leadService: LeadService = LeadService(),
    private val anchorFinder: AnchorFinder = AnchorFinder(),
) {

    fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                checkFeeds()
                delay(15.seconds)
            }
        }
    }

    private suspend fun checkFeeds() {
        val feeds = feedService.readScheduled()
        val now = Clock.System.now()

        data class ConsoleMessage(val core: String, val new: Int, val links: Int, val elements: Int)

        val messages = mutableListOf<ConsoleMessage>()
        for (feed in feeds) {
            val feedSources = mutableListOf<FeedSource>()
            val hrefs = mutableSetOf<String>()
            var new = 0
            if (feed.debug)
                console.log("debugging feed: ${feed.url} with selector: ${feed.selector?.underline()}")
            val webResult = web.fetch(feed.url)
            val content = webResult.content
            val elements = anchorFinder.fromContent(feed, content)
            if (feed.debug)
                console.log("elements: ${elements.size}")

            var linkCount = 0
            for (element in elements) {
                val headline = element.text()
                val href = element.tryGetHrefOrParent() ?: element.tryGetHrefOrChild() ?: continue
                if (headline.isBlank() || (headline[0].isLetter() && !headline[0].isUpperCase())) continue
                if (hrefs.contains(href)) continue
                hrefs.add(href)

                val url = href.toUrlWithContextOrNull(feed.url) ?: continue
                if (url.isLikelyAd()) continue
                val variabilityScore = variabilityScore(url.path)
                if (feed.selector.isNullOrEmpty() && variabilityScore < VARIABILITY_MIN_SCORE) {
                    if (feed.debug) console.log("-variability: ${url.path.take(60)}")
                    continue
                }
                if (feed.external && url.core == feed.url.core) continue
                if (!feed.external && url.core != feed.url.core) continue
                if (feed.debug)
                    console.log("$linkCount: ${headline.take(30)} // ${url.path.take(30)}")
                val (host, hostUrl) = hostAgent.getHost(url)
                // todo: block from robots.txt
                val source = sourceService.readSourceByUrl(url)
                if (source != null) {
                    if (source.type == SourceType.ARTICLE && now - source.existedAt < FAMILIAR_HREF_INTERVAL) {
                        feedSources.add(FeedSource(feedId = feed.id, sourceId = source.id, position = linkCount))
                        linkCount++
                    }
                    continue
                }
                val leadJob = leadService.readLeadInfoByUrl(url)
                if (leadJob != null) {
                    val freshAt = leadJob.freshAt
                    if (freshAt != null && now - freshAt < FAMILIAR_HREF_INTERVAL)
                        linkCount++
                    continue
                }

                val job = LeadJob(
                    feedId = feed.id,
                    headline = headline,
                    isExternal = true,
                    freshAt = Clock.System.now(),
                    feedPosition = if (feed.trackPosition) linkCount++ else null,
                )
                val result = leadMaker.makeLead(hostUrl, job, true)
                if (result == CreateLeadResult.CREATED) new++
            }
            messages.add(ConsoleMessage(feed.url.core, new, linkCount, elements.size))
            val delay = when {
                new == 0 -> 1.hours
                else -> 15.minutes
            }
            feedService.updateFromCheck(feed, now + delay, linkCount, feedSources)
        }
        messages.forEach {
            console.cell(it.core, 20, "core", Justify.LEFT).cell(it.new, 5, "new").cell(it.links, 5, "links")
                .cell(it.elements, 5, "elements").send(background = darkPlumBg)
        }
    }
}

val FAMILIAR_HREF_INTERVAL = 4.days

fun variabilityScore(path: String): Double {
    var score = 1.0
    for (char in path) {
        when (char) {
            '?', '#' -> break
            in 'a'..'z' -> score *= 26
            in 'A'..'Z' -> score *= 52
            in '0'..'9' -> score *= 62
            '/', '-' -> continue
        }
    }
    return score
}

val VARIABILITY_MIN_SCORE = 26.0.pow(20)