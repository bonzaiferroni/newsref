package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.toPink
import newsref.db.log.toYellow
import newsref.model.core.CheckedUrl
import newsref.model.data.Host
import newsref.db.models.CrawlInfo
import newsref.db.services.LeadExistsException
import newsref.db.services.NexusService
import newsref.krawly.utils.TallyMap
import newsref.krawly.utils.increment
import newsref.model.data.LeadJob
import newsref.model.data.FetchResult
import kotlin.time.Duration.Companion.days

class LeadMaker(
	private val hostAgent: HostAgent,
	private val leadService: LeadService = LeadService(),
	private val nexusService: NexusService = NexusService()
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, leadJob: LeadJob? = null): CreateLeadResult {
		return try {
			val lead = leadService.createIfFreshLead(checkedUrl, leadJob)
			if (lead == null) {
				console.logTrace("lead exists: $checkedUrl")
				CreateLeadResult.AFFIRMED
			} else {
				CreateLeadResult.CREATED
			}
		} catch (e: IllegalArgumentException) {
			val urlString = checkedUrl.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: ${checkedUrl.domain}\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			CreateLeadResult.ERROR
		}
	}

	suspend fun makeLeads(fetch: CrawlInfo): TallyMap<CreateLeadResult> {
		val resultMap = mutableMapOf<CreateLeadResult, Int>()
		val page = fetch.page ?: return resultMap
		if (fetch.fetchResult == FetchResult.IRRELEVANT) return resultMap
		val publishedAt = page.article.publishedAt
		if (publishedAt != null && publishedAt < (Clock.System.now() - 30.days)) return resultMap
		val links = fetch.page?.links ?: return resultMap
		for (link in links) {
			val (linkHost, checkedUrl) = hostAgent.getHost(link.url)
			val job = LeadJob(isExternal = link.isExternal, freshAt = fetch.page?.article?.publishedAt)
			val result = makeLead(checkedUrl, job)
			resultMap.increment(result)
		}
		return resultMap
	}
}

enum class CreateLeadResult {
	CREATED,
	AFFIRMED,
	ERROR,
	IRRELEVANT
}