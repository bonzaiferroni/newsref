package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.log.logIfTrue
import newsref.db.log.toCyan
import newsref.db.log.toPink
import newsref.db.models.FetchInfo
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import newsref.model.data.LeadInfo
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class LeadFollower(
	private val web: SpiderWeb,
	private val hostAgent: HostAgent,
	private val leadService: LeadService = LeadService(),
	private val sourceService: SourceService = SourceService(),
	private val leadMaker: LeadMaker = LeadMaker(hostAgent),
	private val maxSpiders: Int = 3,
) {
	private val console = globalConsole.getHandle("LeadFollower", true)

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("checking leads", "🕷  ")
				checkLeads()
				console.logTrace("sleeping", "zzz")
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
			val (freshSince, isExternal, allLeads) = leadService.getOpenLeads().shuffled().filterByTimeStep()
			leads.addAll(allLeads)
			leadCount = leads.size
			refreshed = Clock.System.now()
			val message = "found $leadCount ${isExternal.logIfTrue("(isExternal) ")}leads, fresh since: $freshSince"
			console.logInfo(message.toPink(), leadCount)
		}

		val spiders = ((0 until maxSpiders).map { Spider(it) }).toMutableList()
		val fetches = Collections.synchronizedList(mutableListOf<FetchInfo>())
		val hosts = mutableMapOf<String, Instant>()

		refreshLeads()

		while (!leads.isEmpty()) {

			while (fetches.isNotEmpty()) {
				val fetch = fetches.removeLast()
				sourceService.consume(fetch)
				leadMaker.makeLeads(fetch)
			}

			val now = Clock.System.now()
			if (now - refreshed > 2.minutes)  {
				refreshLeads()
			}

			val lead = leads.removeFirstOrNull() ?: break
			val lastAttempt = hosts[lead.url.domain]
			if (lastAttempt != null && now - lastAttempt < 30.seconds) {
				if (leads.all { it.url.domain == lead.url.domain }) break
				leads.addLast(lead)
				console.status = "😇".padStart(leadCount.toString().length)
				continue
			}
			hosts[lead.url.domain] = now

			while (spiders.isEmpty()) {
				console.status = "🕷".padStart(leadCount.toString().length)
				delay(1.seconds)
			}

			console.status = (--leadCount).toString()
			val spider = spiders.removeLast()
			spider.crawl {
				spider.console.logInfo(lead.url.toString().take(60).toCyan())

				val fetch = spider.sourceReader.read(lead)
				fetches.add(fetch)
				spiders.add(spider)
			}
			delay(100)
		}

		while (spiders.size < maxSpiders) {
			console.status = "🥱"
			delay(1.seconds)
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
}

private fun List<LeadInfo>.filterByTimeStep() =
	this.takeIf { this.size < 2000 }?.let{ Triple(Duration.INFINITE, false, it) }
	?: this.filterSince(1.days, true)
	?: this.filterSince(1.days, false)
	?: this.filterSince(7.days, true)
	?: this.filterSince(7.days, false)
	?: this.filterSince(30.days, true)
	?: this.filterSince(30.days, false)
	?: this.filterSince(365.days, true)
	?: this.filterSince(365.days, false)
	?: Triple(Duration.INFINITE, false, this)

private fun List<LeadInfo>.filterSince(duration: Duration, isExternal: Boolean) =
	this.filter { it.freshAt != null && Clock.System.now() - it.freshAt!! < duration }
		.filter{ if (isExternal) it.isExternal else true }
		.takeIf { it.size > 200 }
		?.let { Triple(duration, isExternal, it) }
