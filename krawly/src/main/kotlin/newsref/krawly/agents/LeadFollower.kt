package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.log.*
import newsref.db.models.CrawlInfo
import newsref.db.models.FetchInfo
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.TallyMap
import newsref.krawly.utils.getCount
import newsref.model.data.LeadInfo
import newsref.model.data.FetchResult
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class LeadFollower(
	private val web: SpiderWeb,
	private val hostAgent: HostAgent,
	private val leadMaker: LeadMaker = LeadMaker(hostAgent),
	private val sourceReader: SourceReader = SourceReader(hostAgent),
	private val leadService: LeadService = LeadService(),
	private val sourceService: SourceService = SourceService(),
) {
	private val maxSpiders: Int = 5
	private val console = globalConsole.getHandle("LeadFollower", true)

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("checking leads", "🕷 ")
				checkLeads()
				console.logTrace("sleeping", "zz")
				delay((10..15).random().seconds)
			}
		}
	}

	private suspend fun checkLeads() {
		val leads = ArrayDeque<LeadInfo>()
		var refreshed = Instant.DISTANT_PAST
		var leadCount = 0

		suspend fun refreshLeads() {
			leads.clear()
			val now = Clock.System.now()
			val allLeads = leadService.getOpenLeads().shuffled().sortedByDescending { lead ->
				lead.freshAt?.let { if (lead.isExternal) it else it - 7.days } ?: (now - 7.days)
			}
			leads.addAll(allLeads)
			val sample = leads.take(1000)
			val avgDaysAgo = sample.sumOf { lead ->
				lead.freshAt?.let { (now - it).toDouble(DurationUnit.DAYS) } ?: 7.0
			} / sample.size
			leadCount = leads.size
			refreshed = Clock.System.now()
			val message = "found leads $leadCount, sample ~${avgDaysAgo.format("%.1f")} days ago"
			allLeads.groupBy { it.url.domain }.toList().sortedByDescending { it.second.size }.take(10).forEach {
				(core, values) -> console.log("${values.size.toString().padStart(4, ' ')}: $core")
			}
			console.logInfo(message.toPink(), leadCount)
		}

		refreshLeads()

		val nest = ((0 until maxSpiders).map { Spider(it) }).toMutableList()
		val fetched = Collections.synchronizedList(mutableListOf<FetchInfo>())
		val hosts = mutableMapOf<String, Instant>()
		startConsumeFetched(fetched)

		while (!leads.isEmpty()) {
			val now = Clock.System.now()
			if (now - refreshed > 2.minutes) {
				refreshLeads()
			}

			val lead = leads.removeFirstOrNull() ?: break
			val nextAttempt = hosts[lead.url.domain]
			if (nextAttempt != null && now < nextAttempt) {
				delay(10)
				leads.addLast(lead)
				console.status = "⌚".padStart(leadCount.toString().length - 1)
				continue
			}
			hosts[lead.url.domain] = now + 30.seconds + (0..30).random().seconds
			console.status = (--leadCount).toString()

			val pastResults = leadService.getResultsByHost(lead.hostId, 100)
			val host = hostAgent.getHost(lead.hostId)

			while (nest.isEmpty()) {
				console.status = "🕷".padStart(leadCount.toString().length - 1)
				delay(1.seconds)
			}

			val spider = nest.removeLast()
			spider.crawl {
				val newFetch = spider.sourceFetcher.fetch(lead, host, pastResults)
				fetched.add(newFetch)
				nest.add(spider) // return home, spidey
			}
			delay(100)
		}

		while (nest.size < maxSpiders) {
			console.status = "🥱"
			delay(1.seconds)
		}
	}

	private fun startConsumeFetched(fetched: MutableList<FetchInfo>) {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				val fetch = fetched.removeLastOrNull()
				if (fetch != null) {
					val crawl = sourceReader.read(fetch)
					sourceService.consume(crawl)
					val resultMap = leadMaker.makeLeads(crawl)
					logFetch(crawl, resultMap)
				}
				delay(100)
			}
		}
	}

	inner class Spider(
		spindex: Int,
		val sourceFetcher: SourceFetcher = SourceFetcher(spindex, web)
	) {
		val console = globalConsole.getHandle("spider $spindex")

		fun crawl(block: suspend () -> Unit) {
			CoroutineScope(Dispatchers.Default).launch {
				try {
					block()
				} catch (e: Exception) {
					console.logError("Spider found exception:\n${e}")
				}
			}
		}
	}

	private var alternateBg = false

	private fun logFetch(crawl: CrawlInfo, tally: TallyMap<CreateLeadResult>) {
		val resultMap = crawl.fetch.pastResults.groupingBy { it.result }.eachCount()
		val lead = crawl.fetch.lead
		val resultType = crawl.fetchResult
		val rowWidth = 64
		val createdLeads = tally.getCount(CreateLeadResult.CREATED)
		val strategyMsg = crawl.fetch.failedStrategy?.let {
			"${it.toString().take(2)}⇒${crawl.fetch.strategy.toString().take(2)}"
		} ?: crawl.fetch.strategy?.toString() ?: "SKIP"

		val urlMsg = "${lead.url.toString().take(rowWidth - 1)}${(lead.url.length > rowWidth - 1).logIfTrue("~")}".let {
			return@let when (crawl.fetch.result?.isOk) {
				true -> it.toCyan()
				false -> it.toOrange()
				null -> it.dim()
			}
		}

		val background = alternateBg.also { alternateBg = !alternateBg }.let {
			if (it) forestNightBg else deepspaceBlueBg
		}
		console.row(urlMsg, background = background, width = rowWidth)

		val page = crawl.page
		if (page == null) {
			console.cell("", justify = Justify.LEFT, width = 40)
				.cell(strategyMsg, 5, justify = Justify.LEFT)
				.cell(
					"${resultMap.getResult(FetchResult.RELEVANT)}", 5, "relevant",
					highlight = resultType == FetchResult.RELEVANT
				)
				.cell(
					"${resultMap.getResult(FetchResult.IRRELEVANT)}", 5, "irrelevant",
					highlight = resultType == FetchResult.IRRELEVANT
				)
				.cell(
					"${resultMap.getResult(FetchResult.TIMEOUT)}", 5, "timeout",
					highlight = resultType == FetchResult.TIMEOUT
				)
				.row(background = background, width = rowWidth)
			return
		}

		val title = page.article.headline
		console.cell(title, rowWidth, justify = Justify.LEFT)
			.row(background = background, width = rowWidth)

		val externalLinkCount = page.links.count { it.isExternal }
		console
			.cell(page.type.getEmoji())
			.cell("📰") { page.foundNewsArticle }
			.cell("🌆") { page.article.imageUrl != null }
			.cell("📝") { page.article.description != null }
			.cell("📅") { page.article.publishedAt != null }
			.cell("🦦") { page.authors != null }
			.cell("", justify = Justify.LEFT, width = 3)
			.cell(page.article.wordCount.toString(), 7, "words")
			.cell("$createdLeads/$externalLinkCount/${page.links.size}", 10, "links")
			.cell(strategyMsg, 5, justify = Justify.LEFT)
			.cell(
				"${resultMap.getResult(FetchResult.RELEVANT)}", 5, "relevant",
				highlight = resultType == FetchResult.RELEVANT
			)
			.cell(
				"${resultMap.getResult(FetchResult.IRRELEVANT)}", 5, "irrelevant",
				highlight = resultType == FetchResult.IRRELEVANT
			)
			.cell(
				"${resultMap.getResult(FetchResult.TIMEOUT)}", 5, "timeout",
				highlight = resultType == FetchResult.TIMEOUT
			)
			.row(background = background, width = rowWidth)
	}
}

fun Double.format(format: String) = format.format(this)