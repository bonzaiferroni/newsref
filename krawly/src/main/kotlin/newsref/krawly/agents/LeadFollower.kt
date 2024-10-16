package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.log.toCyan
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import newsref.model.data.LeadInfo
import kotlin.time.Duration.Companion.seconds

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
				console.logTrace("checking leads", "🕷  ")
				checkLeads()
				console.logTrace("sleeping", "zzz")
				delay((10..15).random().seconds)
			}
		}
	}

	private suspend fun checkLeads() {
		val allLeads = leadService.getOpenLeads().shuffled()
		val stack = ArrayDeque(allLeads.filter { it.isExternal })
		if (stack.isEmpty()) {
			console.logInfo("No external leads available, following other leads")
			stack.addAll(allLeads)
		}
		var leadCount = stack.size
		console.logTrace("found ${stack.size} jobs", leadCount)
		val hosts = mutableMapOf<String, Instant>()
		val spiders = ArrayDeque((0 until maxSpiders).map { Spider(it) })
		while (!stack.isEmpty()) {
			val lead = stack.removeFirstOrNull() ?: break
			val now = Clock.System.now()
			val lastAttempt = hosts[lead.url.domain]
			if (lastAttempt != null && now - lastAttempt < 30.seconds) {
				if (stack.all { it.url.domain == lead.url.domain }) break
				stack.addLast(lead)
				continue
			}
			hosts[lead.url.domain] = now

			while (spiders.isEmpty()) {
				delay(1.seconds)
			}

			val spider = spiders.removeFirst()
			spider.crawl {
				spider.console.logInfo(lead.url.toString().toCyan(), --leadCount)

				val fetch = spider.sourceReader.read(lead)
				sourceService.consume(fetch)

				val count = leadMaker.makeLeads(fetch)
				spider.console.logInfo("found $count new leads from ${lead.url.domain}")
				spiders.add(spider)
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
				block()
			}
		}
	}
}