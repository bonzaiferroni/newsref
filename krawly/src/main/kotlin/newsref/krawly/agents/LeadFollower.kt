package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.log.toCyan
import newsref.db.log.toPink
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import newsref.model.data.LeadInfo
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
		val (freshSince, allLeads) = leadService.getOpenLeads().shuffled().filterByTimeStep()
		val stack = ArrayDeque(allLeads)
		if (stack.isEmpty()) {
			console.logInfo("No external leads available, following other leads")
			stack.addAll(allLeads)
		}
		var leadCount = stack.size
		console.logInfo("found ${stack.size} jobs, fresh since: $freshSince".toPink(), leadCount)
		val hosts = mutableMapOf<String, Instant>()
		val spiders = ArrayDeque((0 until maxSpiders).map { Spider(it) })
		val started = Clock.System.now()
		while (!stack.isEmpty()) {
			delay(100)
			val lead = stack.removeFirstOrNull() ?: break
			val now = Clock.System.now()
			val lastAttempt = hosts[lead.url.domain]
			if (lastAttempt != null && now - lastAttempt < 30.seconds) {
				if (stack.all { it.url.domain == lead.url.domain }) break
				stack.addLast(lead)
				console.status = "😇".padStart(leadCount.toString().length)
				continue
			}
			hosts[lead.url.domain] = now

			while (spiders.isEmpty()) {
				console.status = "😪".padStart(leadCount.toString().length)
				delay(1.seconds)
			}

			console.status = (--leadCount).toString()
			val spider = spiders.removeFirst()
			spider.crawl {
				spider.console.logInfo(lead.url.toString().take(60).toCyan())

				val fetch = spider.sourceReader.read(lead)
				sourceService.consume(fetch)

				val count = leadMaker.makeLeads(fetch)
				// spider.console.logInfo("found $count new leads from ${lead.url.domain}")
				spiders.add(spider)
			}

			if (now - started > 10.minutes) break
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
				block()
			}
		}
	}
}

private fun List<LeadInfo>.filterByTimeStep() = this.takeIf { this.size < 5000 }?.let{ Pair(Duration.INFINITE, it) }
	?: this.filterExternal().filterSince(1.days)
	?: this.filterSince(1.days)
	?: this.filterExternal().filterSince(7.days)
	?: this.filterSince(7.days)
	?: this.filterExternal().filterSince(30.days)
	?: this.filterSince(30.days)
	?: this.filterExternal().filterSince(365.days)
	?: this.filterSince(365.days)
	?: Pair(Duration.INFINITE, this)

private fun List<LeadInfo>.filterSince(duration: Duration) =
	this.filter { it.freshAt != null && Clock.System.now() - it.freshAt!! < duration }.takeIf { it.size > 500 }
		?.let { Pair(duration, it) }

private fun List<LeadInfo>.filterExternal() = this.filter { it.isExternal }