package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.toPink
import newsref.db.log.toYellow
import newsref.model.core.SourceType
import newsref.model.data.LeadJob
import newsref.model.dto.SourceInfo

class LeadMaker(
	val outletAgent: OutletAgent,
	private val leadService: LeadService = LeadService()
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(leadJob: LeadJob): LeadJob? {
		if (leadService.leadExists(leadJob.url)) return null
		return try {
			leadService.createIfFreshLead(leadJob)								//    LeadService ->
		} catch (e: IllegalArgumentException) {
			val urlString = leadJob.url.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: $urlString\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			null
		}
	}

	suspend fun makeLeads(sourceInfo: SourceInfo): Int {
		if (sourceInfo.source.type != SourceType.ARTICLE) return 0
		val newLeads = sourceInfo.document?.links?.map { LeadJob(url = it.url) }
			?: return 0
		return makeLeads(newLeads)
	}

	suspend fun makeLeads(jobs: List<LeadJob>): Int {
		var newLeadCount = 0
		for (job in jobs) {
			val newJob = makeLead(job)
			if (newJob != null) newLeadCount++
		}
		return newLeadCount
	}
}