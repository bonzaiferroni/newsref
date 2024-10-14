package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.toPink
import newsref.db.log.toYellow
import newsref.model.core.CheckedUrl
import newsref.model.data.Lead
import newsref.model.data.Host
import newsref.model.dto.FetchInfo

class LeadMaker(
	val hostAgent: HostAgent,
	private val leadService: LeadService = LeadService()
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, host: Host, headline: String): Lead? {
		if (leadService.leadExists(checkedUrl)) return null
		return try {
			leadService.createIfFreshLead(checkedUrl, host)								//    LeadService ->
		} catch (e: IllegalArgumentException) {
			val urlString = checkedUrl.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: $urlString\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			null
		}
	}

	suspend fun makeLeads(fetchInfo: FetchInfo): Int {
		throw NotImplementedError()
//		if (fetchInfo.source.type != SourceType.ARTICLE) return 0
//		val newLeads = fetchInfo.page?.links?.map { Lead(url = it.url) }
//			?: return 0
//		return makeLeads(newLeads)
	}
}