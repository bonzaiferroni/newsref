package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.log.toCyan
import newsref.db.services.LeadService
import newsref.krawly.SpiderWeb
import kotlin.time.Duration.Companion.seconds

class LeadFollower(
	private val web: SpiderWeb,
	private val leadMaker: LeadMaker,
	private val hostAgent: HostAgent,
	private val sourceAgent: SourceAgent = SourceAgent(web, hostAgent),
	private val leadService: LeadService = LeadService()
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
		val jobs = leadService.getOpenLeads()
		var leadCount = jobs.size
		console.logTrace("found ${jobs.size} jobs", leadCount)
		val hosts = mutableSetOf<String>()
		for (job in jobs) {
			if (hosts.contains(job.url.domain)) { continue }
			hosts.add(job.url.domain)

			console.logInfo(job.url.toString().toCyan(), --leadCount)

			val sourceInfo = sourceAgent.read(job)

			val count = leadMaker.makeLeads(sourceInfo)
			console.logInfo("found $count new leads from ${job.url.domain}")
		}
	}
}