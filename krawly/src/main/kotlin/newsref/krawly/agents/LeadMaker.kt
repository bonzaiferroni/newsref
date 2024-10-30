package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.toPink
import newsref.db.log.toYellow
import newsref.model.core.CheckedUrl
import newsref.db.models.CrawlInfo
import newsref.db.services.CreateLeadResult
import newsref.db.services.NexusService
import newsref.krawly.utils.TallyMap
import newsref.krawly.utils.increment
import newsref.model.data.LeadJob
import newsref.model.data.FetchResult
import kotlin.time.Duration.Companion.days

class LeadMaker(
	private val leadService: LeadService = LeadService(),
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, leadJob: LeadJob? = null, createIfFresh: Boolean): CreateLeadResult {
		return try {
			leadService.createOrLinkLead(checkedUrl, leadJob, createIfFresh)
		} catch (e: IllegalArgumentException) {
			val urlString = checkedUrl.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: ${checkedUrl.core}\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			CreateLeadResult.ERROR // return
		}
	}

	suspend fun makeLeads(fetch: CrawlInfo): TallyMap<CreateLeadResult> {
		val resultMap = mutableMapOf<CreateLeadResult, Int>()
		val page = fetch.page ?: return resultMap
		val links = fetch.page?.links ?: return resultMap
		for (link in links) {
			val publishedAt = page.article.publishedAt
			val freshSource = publishedAt != null && publishedAt > (Clock.System.now() - 30.days)
			val createIfFresh = fetch.fetchResult == FetchResult.RELEVANT && freshSource
			val job = LeadJob(isExternal = link.isExternal, freshAt = fetch.page?.article?.publishedAt)
			val result = makeLead(link.url, job, createIfFresh)
			resultMap.increment(result)
		}
		return resultMap
	}
}