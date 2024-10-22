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
import newsref.krawly.utils.TallyMap
import newsref.krawly.utils.increment
import newsref.model.core.ArticleType
import newsref.model.core.SourceType
import newsref.model.data.LeadJob
import newsref.model.data.ResultType
import kotlin.time.Duration.Companion.days

class LeadMaker(
	private val hostAgent: HostAgent,
	private val leadService: LeadService = LeadService()
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, host: Host, leadJob: LeadJob? = null): CreateLeadResult {
		return try {
			leadService.createIfFreshLead(checkedUrl, host, leadJob)
			CreateLeadResult.CREATED
		} catch(e: LeadExistsException) {
			console.logTrace("lead exists: $checkedUrl")
			CreateLeadResult.AFFIRMED
		} catch (e: IllegalArgumentException) {
			val urlString = checkedUrl.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: ${checkedUrl.domain}\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			CreateLeadResult.ERROR
		}
	}

	suspend fun makeLeads(fetch: FetchInfo): TallyMap<CreateLeadResult> {
		val resultMap = mutableMapOf<CreateLeadResult, Int>()
		val page = fetch.page ?: return resultMap
		if (fetch.resultType == ResultType.IRRELEVANT) return resultMap
		val publishedAt = page.article.publishedAt
		if (publishedAt != null && publishedAt < (Clock.System.now() - 30.days)) return resultMap
		val links = fetch.page?.links ?: return resultMap
		for (link in links) {
			if (!link.isExternal && !page.foundNewsArticle) {
				resultMap.increment(CreateLeadResult.IRRELEVANT)
				continue
			}
			val (host, checkedUrl) = hostAgent.getHost(link.url)
			val job = LeadJob(isExternal = link.isExternal, freshAt = fetch.page?.article?.publishedAt)
			val result = makeLead(checkedUrl, host, job)
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