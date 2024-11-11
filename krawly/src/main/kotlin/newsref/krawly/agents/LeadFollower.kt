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
import newsref.db.services.CreateLeadResult
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.db.utils.format
import newsref.db.utils.profile
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.TallyMap
import newsref.krawly.utils.getCount
import newsref.krawly.utils.isNetworkAvailable
import newsref.model.data.LeadInfo
import newsref.model.data.FetchResult
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class LeadFollower(
	private val web: SpiderWeb,
	private val hostAgent: HostAgent,
	private val leadMaker: LeadMaker = LeadMaker(),
	private val sourceReader: SourceReader = SourceReader(web, hostAgent),
	private val nexusFinder: NexusFinder = NexusFinder(),
	private val leadService: LeadService = LeadService(),
	private val sourceService: SourceService = SourceService(),
) {
	private val maxSpiders: Int = 10
	private val console = globalConsole.getHandle("LeadFollower", true)
	private val fetched = Collections.synchronizedList(mutableListOf<FetchInfo>())
	private val nest = Collections.synchronizedList(((0 until maxSpiders).map { Spider(it) }).toMutableList())
	private val leads = LinkedList<LeadInfo>()
	private val hosts = mutableMapOf<String, Instant>()
	private var refreshed = Instant.DISTANT_PAST
	private var followCount = 0

	private var _leadCount = 0
	private var leadCount
		get() = _leadCount
		set(value) { _leadCount = value; refreshStatus() }
	private var _icon = "🙈"
	private var icon
		get() = _icon
		set(value) { _icon = value; refreshStatus() }
	private val statusLine get() =
		"${leadCount.toString().padStart(5)} $icon ${nest.size.toString().padStart(2)}/$maxSpiders 🐛 ${fetched.size} 📖"
	private fun refreshStatus() { console.status = statusLine }

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("checking leads")
				checkLeads()
				console.logTrace("sleeping")
				delay((10..15).random().seconds)
			}
		}
		startConsumeFetched()
	}

	private suspend fun refreshLeads() {
		while (fetched.isNotEmpty()) {
			icon = "📖"
			delay(10)
		}

		leads.clear()
		val now = Clock.System.now()
		val allLeads = leadService.getOpenLeads()
		leads.addAll(allLeads)
		if (allLeads.isEmpty()) return
		val sample = leads.take(200)
		val avgDaysAgo = sample.sumOf { lead ->
			lead.freshAt?.let { (now - it).toDouble(DurationUnit.DAYS) } ?: 7.0
		} / sample.size
		leadCount = leads.size
		refreshed = Clock.System.now()
		val message = "followed $followCount, found $leadCount, sample ~${avgDaysAgo.format(1)} days ago"
		followCount = 0
		console.logInfo(message.toPink(), leadCount)
		val top = allLeads.first()
		console.log("top: ${top.linkCount} links, isExternal ${top.isExternal}\n${top.url}".toGreenBg())
		allLeads.groupBy { it.url.domain }.toList().sortedByDescending { it.second.size }.take(10).forEach {
				(core, values) -> console.log("${values.size.toString().padStart(4, ' ')}: $core")
		}
	}

	private fun getNextLead(): LeadInfo? {
		val now = Clock.System.now()
		for ((index, lead) in leads.withIndex()) {
			val nextAttempt = hosts[lead.url.core]
			if (nextAttempt != null && now < nextAttempt) continue
			hosts[lead.url.core] = now + 45.seconds + (0..30).random().seconds
			leads.removeAt(index)
			return lead
		}
		return null
	}

	private suspend fun checkLeads() {
		refreshLeads()
		while (leads.isNotEmpty()) {
			val now = Clock.System.now()
			if (now - refreshed > 2.minutes) {
				refreshLeads()
			}

			val lead = getNextLead()
			if (lead == null) {
				icon = "🍃"
				delay(100)
				continue
			}

			val pastResults = leadService.getResultsByHost(lead.hostId, 100)
			val (host, url) = hostAgent.getHost(lead.url)

			while (nest.isEmpty()) {
				icon = "🐛"
				delay(10)
			}

			--leadCount; icon = "👾"
			followCount++

			val spider = nest.removeLast()
			spider.crawl {
				val newFetch = spider.sourceFetcher.fetch(lead, url, host, pastResults)
				if (newFetch.result?.noConnection != true || isNetworkAvailable()) {
					fetched.add(newFetch)
				} else {
					console.logError("Choppy waters! (internet unavailable)")
				}
				nest.add(spider) // return home, spidey
			}
			delay(100)
		}

		while (nest.size < maxSpiders) {
			icon = "🥱"
			delay(10)
		}
	}

	private fun startConsumeFetched() {
		CoroutineScope(Dispatchers.IO).launch {
			while (true) {
				val fetch = fetched.removeLastOrNull()
				if (fetch != null) {
					try {
						hostAgent.updateParameters(fetch.leadHost, fetch.junkParams, fetch.navParams)
						var crawl = sourceReader.read(fetch)
						val pageHost = crawl.page?.pageHost
						val junkParams = crawl.cannonJunkParams
						if (pageHost != null && junkParams != null)
							hostAgent.updateParameters(pageHost, null, junkParams)
						crawl = nexusFinder.findNexuses(crawl)

						// consume source
						val id = sourceService.consume(crawl)
						val resultMap = leadMaker.makeLeads(crawl)
						logFetch(crawl, id, resultMap)
					} catch (e: Exception) {
						console.logError("Error consuming fetch:\n${fetch.lead.url}\n$e")
					}
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

	private fun logFetch(crawl: CrawlInfo, sourceId: Long, tally: TallyMap<CreateLeadResult>) {
		val resultMap = crawl.fetch.pastResults.groupingBy { it.result }.eachCount()
		val lead = crawl.fetch.lead
		val resultType = crawl.fetchResult
		val rowWidth = 64
		val createdLeads = tally.getCount(CreateLeadResult.CREATED)
		val strategyMsg = crawl.fetch.failedStrategy?.let {
			"${it.toString().take(2)}~${crawl.fetch.strategy.toString().take(2)}"
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
			console.cell("", justify = Justify.LEFT, width = 22)
				.cell(resultMap.getResult(FetchResult.SKIPPED), 5, "skip",
					highlight = resultType == FetchResult.SKIPPED
				)
				.cell(resultMap.getResult(FetchResult.UNKNOWN), 5, "unknown",
					highlight = resultType == FetchResult.UNKNOWN
				)
				.cell(resultMap.getResult(FetchResult.ERROR), 5, "error",
					highlight = resultType == FetchResult.ERROR
				)
				.cell(strategyMsg, 5, justify = Justify.LEFT)
				.cell(
					resultMap.getResult(FetchResult.RELEVANT), 5, "relevant",
					highlight = resultType == FetchResult.RELEVANT
				)
				.cell(
					resultMap.getResult(FetchResult.IRRELEVANT), 5, "irrelevant",
					highlight = resultType == FetchResult.IRRELEVANT
				)
				.cell(
					resultMap.getResult(FetchResult.TIMEOUT), 5, "timeout",
					highlight = resultType == FetchResult.TIMEOUT
				)
				.row(background = background, width = rowWidth)
			return
		}

		val title = page.article?.headline ?: page.source.title ?: "[No title]"
		console.cell(title, rowWidth, justify = Justify.LEFT)
			.row(background = background, width = rowWidth)
		console.cell("http://localhost:3000/#/source/$sourceId", justify = Justify.LEFT)
			.row(background = background, width = rowWidth)
		val externalLinkCount = page.links.count { it.isExternal }
		console
			.cell(page.source.type?.getEmoji() ?: "💢")
			.cell("📰") { page.foundNewsArticle }
			.cell("🌆") { page.source.imageUrl != null }
			.cell("💅") { page.source.thumbnail != null }
			.cell("📅") { page.source.publishedAt != null }
			.cell("🦦") { page.authors != null }
			.cell("🔗") { page.authors?.firstOrNull()?.url != null }
			.cell("🍓") { page.isFresh }
			.cell((page.article?.wordCount ?: page.source.wordCount).toString(), 7, "words")
			.cell(createdLeads, 2, "leads", highlight = createdLeads > 0)
			.cell("$externalLinkCount/${page.links.size}", 5, "links")
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

