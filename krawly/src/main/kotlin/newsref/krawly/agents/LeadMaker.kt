package newsref.krawly.agents

import newsref.db.services.LeadService
import newsref.krawly.console
import newsref.krawly.log.LogConsole
import newsref.model.data.LeadJob
import newsref.model.data.SourceType
import newsref.model.dto.SourceInfo

class LeadMaker(
	private val leadService: LeadService = LeadService()
) {

	suspend fun makeLeads(sourceInfo: SourceInfo): Int {
		if (sourceInfo.source.type != SourceType.ARTICLE) return 0
		val newLeads = sourceInfo.document?.links?.map { LeadJob(url = it.url) }
			?: return 0
		return makeLeads(newLeads)
	}

	suspend fun makeLeads(jobs: List<LeadJob>): Int {
		var newLeadCount = 0
		for (job in jobs) {
			if (leadService.leadExists(job.url)) continue
			try {
				leadService.createIfFreshLead(job)								//    LeadService ->
				newLeadCount++
			} catch (e: IllegalArgumentException) {
				console.logWarning("LeadMaker", e.message ?: "Error creating job: $job")
			}
		}
		return newLeadCount
	}
}