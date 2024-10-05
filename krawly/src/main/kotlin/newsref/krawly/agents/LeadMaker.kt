package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.LogConsole
import newsref.model.data.LeadJob
import newsref.model.data.SourceType
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
			console.logWarning(e.message ?: "Error creating job: $leadJob")
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