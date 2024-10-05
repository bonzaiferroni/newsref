package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.services.LeadService
import newsref.db.utils.cacheResource
import newsref.krawly.MAX_URL_ATTEMPTS
import newsref.krawly.SpiderWeb
import newsref.krawly.log.LogConsole
import kotlin.time.Duration.Companion.seconds

class LeadFollower(
	console: LogConsole,
	private val web: SpiderWeb,
	private val leadMaker: LeadMaker,
	private val outletAgent: OutletAgent,
	private val sourceAgent: SourceAgent = SourceAgent(outletAgent),
	private val leadService: LeadService = LeadService()
) {
	private val console = console.getHandle("LeadFollower")

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.log("checking leads", "🕷  ")
				checkLeads()
				console.log("sleeping", "zzz")
				delay((10..15).random().seconds)
			}
		}
	}

	private suspend fun checkLeads() {
		val jobs = leadService.getJobs()
		var leadCount = jobs.size
		console.log("found ${jobs.size} jobs", leadCount)
		val hosts = mutableSetOf<String>()
		for (job in jobs) {
			if (hosts.contains(job.url.host)) { continue }
			hosts.add(job.url.host)

			console.logDebug("attempting: ${job.url.host}", --leadCount)
			leadService.addAttempt(job)
			val result = web.crawlPage(job.url, true)
			if (result == null || !result.isSuccess()) {
				console.logWarning("crawl fail (${result?.status}): ${job.url}")
				result?.screenshot?.cacheResource(job.url, "png", "nav_fail")

				// keep job and try a few times
				if (job.attemptCount < MAX_URL_ATTEMPTS) { continue }
			}
			result?.screenshot?.cacheResource(job.url, "png")
			result?.doc?.html?.cacheResource(job.url, "html", "content")

			val sourceInfo = sourceAgent.read(job, result?.doc)
			leadService.addSource(job.leadId, sourceInfo.id)                    //    LeadService ->

			val count = leadMaker.makeLeads(sourceInfo)
			console.log("found $count new leads from ${job.url.host}")
		}
	}
}