package newsref.krawly.agents

import jdk.jshell.spi.ExecutionControl.NotImplementedException
import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.toPink
import newsref.db.log.toYellow
import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.data.Lead
import newsref.model.data.LeadJob
import newsref.model.data.Outlet
import newsref.model.dto.FetchInfo

class LeadMaker(
	val outletAgent: OutletAgent,
	private val leadService: LeadService = LeadService()
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, outlet: Outlet): Lead? {
		if (leadService.leadExists(checkedUrl)) return null
		return try {
			leadService.createIfFreshLead(checkedUrl, outlet)								//    LeadService ->
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