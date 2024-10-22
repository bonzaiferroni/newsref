package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.log.*
import newsref.db.models.FetchInfo
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.TallyMap
import newsref.krawly.utils.getCount
import newsref.model.data.LeadInfo
import newsref.model.data.ResultType
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class LeadFollower(
	private val web: SpiderWeb,
	private val hostAgent: HostAgent,
	private val leadService: LeadService = LeadService(),
	private val sourceService: SourceService = SourceService(),
	private val leadMaker: LeadMaker = LeadMaker(hostAgent),
	private val maxSpiders: Int = 5,
) {
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
			val sample = leads.take(200)
			val avgDaysAgo = sample.sumOf { lead ->
				lead.freshAt?.let { (now - it).toDouble(DurationUnit.DAYS) } ?: 7.0
			} / sample.size
			leadCount = leads.size
			refreshed = Clock.System.now()
			val message = "found leads $leadCount, sample ~${avgDaysAgo.format("%.1f")} days ago"
			console.logInfo(message.toPink(), leadCount)
		}

		val spiders = ((0 until maxSpiders).map { Spider(it) }).toMutableList()
		val fetched = Collections.synchronizedList(mutableListOf<FetchInfo>())
		val hosts = mutableMapOf<String, Instant>()

		refreshLeads()

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

			while (spiders.isEmpty()) {
				console.status = "🕷".padStart(leadCount.toString().length - 1)
				delay(1.seconds)
			}

			console.status = (--leadCount).toString()
			val spider = spiders.removeLast()
			spider.crawl {
				val newFetch = spider.sourceReader.read(lead)
				fetched.add(newFetch)
				spiders.add(spider)
			}
			delay(100)
		}

		while (spiders.size < maxSpiders) {
			console.status = "🥱"
			delay(1.seconds)
		}
	}

	private fun startConsumeFetched(fetched: MutableList<FetchInfo>) {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				val fetch = fetched.removeLastOrNull()
				if (fetch != null) {
					sourceService.consume(fetch)
					val resultMap = leadMaker.makeLeads(fetch)
					logFetch(fetch, resultMap)
				}
				delay(100)
			}
		}
	}

	inner class Spider(
		spindex: Int,
		val sourceReader: SourceReader = SourceReader(spindex, web, hostAgent),
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

	private fun logFetch(fetch: FetchInfo, tally: TallyMap<CreateLeadResult>) {
		val resultMap = fetch.resultMap
		val lead = fetch.lead
		val resultType = fetch.resultType
		val rowWidth = 64
		val createdLeads = tally.getCount(CreateLeadResult.CREATED)

		val urlMsg = "${lead.url.toString().take(rowWidth - 1)}${(lead.url.length > rowWidth - 1).logIfTrue("~")}".let {
			return@let when (fetch.statusOk) {
				true -> it.toCyan()
				false -> it.toOrange()
				null -> it.dim()
			}
		}

		val background = alternateBg.also { alternateBg = !alternateBg }.let {
			if (it) forestNightBg else deepspaceBlueBg
		}
		console.row(urlMsg, background = background, width = rowWidth)

		val page = fetch.page
		if (page == null) {
			console.cell("", justify = Justify.LEFT, width = 46)
				.cell(
					"${resultMap.getResult(ResultType.RELEVANT)}", 5, "relevant",
					highlight = resultType == ResultType.RELEVANT
				)
				.cell(
					"${resultMap.getResult(ResultType.IRRELEVANT)}", 5, "irrelevant",
					highlight = resultType == ResultType.IRRELEVANT
				)
				.cell(
					"${resultMap.getResult(ResultType.TIMEOUT)}", 5, "timeout",
					highlight = resultType == ResultType.TIMEOUT
				)
				.row(background = background, width = rowWidth)
			return
		}

		val title = fetch.page?.article?.headline ?: fetch.lead.feedHeadline ?: ""
		console.cell(title, rowWidth, justify = Justify.LEFT)
			.row(background = background, width = rowWidth)

		val externalLinkCount = page.links.count { it.isExternal }
		console
			.cell(fetch.source.type?.getEmoji() ?: "💢")
			.cell("📰") { page.foundNewsArticle }
			.cell("🌆") { page.article.imageUrl != null }
			.cell("📝") { page.article.description != null }
			.cell("📅") { page.article.publishedAt != null }
			.cell("🦦") { page.authors != null }
			.cell("", justify = Justify.LEFT, width = 9)
			.cell(page.article.wordCount.toString(), 7, "words")
			.cell("$createdLeads/$externalLinkCount/${page.links.size}", 10, "links")
			.cell(
				"${resultMap.getResult(ResultType.RELEVANT)}", 5, "relevant",
				highlight = resultType == ResultType.RELEVANT
			)
			.cell(
				"${resultMap.getResult(ResultType.IRRELEVANT)}", 5, "irrelevant",
				highlight = resultType == ResultType.IRRELEVANT
			)
			.cell(
				"${resultMap.getResult(ResultType.TIMEOUT)}", 5, "timeout",
				highlight = resultType == ResultType.TIMEOUT
			)
			.row(background = background, width = rowWidth)
	}
}

fun Double.format(format: String) = format.format(this)