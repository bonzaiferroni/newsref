package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.log.toCyan
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import kotlin.time.Duration.Companion.seconds

class LeadFollower(
	private val web: SpiderWeb,
	private val leadMaker: LeadMaker,
	private val hostAgent: HostAgent,
	private val sourceReader: SourceReader = SourceReader(web, hostAgent),
	private val leadService: LeadService = LeadService(),
	private val sourceService: SourceService = SourceService(),
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
		val allLeads = leadService.getOpenLeads()
		var leads = allLeads.filter { it.isExternal }
		if (leads.isEmpty()) {
			console.logInfo("No external leads available, following other leads")
			leads = allLeads
		}
		var leadCount = leads.size
		console.logTrace("found ${leads.size} jobs", leadCount)
		val hosts = mutableSetOf<String>()
		for (job in allLeads) {
			if (hosts.contains(job.url.domain)) {
				continue
			}
			hosts.add(job.url.domain)

			console.logInfo(job.url.toString().toCyan(), --leadCount)

			val fetch = sourceReader.read(job)
			sourceService.consume(fetch)

			val count = leadMaker.makeLeads(fetch)
			console.logInfo("found $count new leads from ${job.url.domain}")
		}
	}
}