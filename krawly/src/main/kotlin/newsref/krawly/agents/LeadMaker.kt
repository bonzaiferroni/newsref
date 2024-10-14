package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.toPink
import newsref.db.log.toYellow
import newsref.model.core.CheckedUrl
import newsref.model.data.Lead
import newsref.model.data.Host
import newsref.db.models.FetchInfo
import newsref.model.data.FeedJob

class LeadMaker(
	val hostAgent: HostAgent,
	private val leadService: LeadService = LeadService()
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, host: Host, feedJob: FeedJob? = null): Lead? {
		if (leadService.leadExists(checkedUrl)) return null
		return try {
			leadService.createIfFreshLead(checkedUrl, host, feedJob)
		} catch (e: IllegalArgumentException) {
			val urlString = checkedUrl.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: $urlString\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			null
		}
	}

	suspend fun makeLeads(fetchInfo: FetchInfo): Int {
		val links = fetchInfo.page?.links ?: return 0
		for (link in links) {
			val host = hostAgent.getHost(link.url)

		}
		throw NotImplementedError()
//		if (fetchInfo.source.type != SourceType.ARTICLE) return 0
//		val newLeads = fetchInfo.page?.links?.map { Lead(url = it.url) }
//			?: return 0
//		return makeLeads(newLeads)
	}
}