package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.log.toPink
import newsref.db.log.toYellow
import newsref.model.core.CheckedUrl
import newsref.model.data.Lead
import newsref.model.data.Host
import newsref.db.models.FetchInfo
import newsref.db.services.LeadExistsException
import newsref.model.core.ArticleType
import newsref.model.core.SourceType
import newsref.model.data.LeadJob
import kotlin.time.Duration.Companion.days

class LeadMaker(
	private val hostAgent: HostAgent,
	private val leadService: LeadService = LeadService()
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, host: Host, leadJob: LeadJob? = null): Lead? {
		return try {
			leadService.createIfFreshLead(checkedUrl, host, leadJob)
		} catch(e: LeadExistsException) {
			console.logTrace("lead exists: $checkedUrl")
			null
		} catch (e: IllegalArgumentException) {
			val urlString = checkedUrl.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: ${checkedUrl.domain}\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			null
		}
	}

	suspend fun makeLeads(fetch: FetchInfo): Int {
		val page = fetch.page ?: return 0
		if (fetch.source.type != SourceType.ARTICLE || page.articleType != ArticleType.NEWS) return 0
		val publishedAt = page.article.publishedAt
		if (publishedAt != null && publishedAt < (Clock.System.now() - 30.days)) return 0
		val links = fetch.page?.links ?: return 0
		var leadCount = 0
		for (link in links) {
			val (host, checkedUrl) = hostAgent.getHost(link.url)
			val job = LeadJob(isExternal = true, freshAt = fetch.page?.article?.publishedAt)
			val lead = makeLead(checkedUrl, host, job)
			if (lead != null) leadCount++
		}
		return leadCount
	}
}