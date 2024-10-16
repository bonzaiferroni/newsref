package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.utils.cacheResource
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.WebResult
import newsref.krawly.utils.isFile
import newsref.model.core.SourceType
import newsref.model.core.Url
import newsref.model.data.LeadInfo
import newsref.model.data.ResultType
import newsref.model.data.Source
import newsref.db.models.FetchInfo
import newsref.db.models.PageInfo
import newsref.krawly.utils.toMarkdown
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class SourceReader(
    spindex: Int,
	private val web: SpiderWeb,
	private val hostAgent: HostAgent,
	private val pageReader: PageReader = PageReader(spindex, hostAgent),
	private val leadService: LeadService = LeadService(),
) {
    private val console = globalConsole.getHandle("Src $spindex")

    suspend fun read(lead: LeadInfo): FetchInfo {
        val resultMap = leadService.getResultsByOutlet(lead.hostId, 1.hours)
        val relevantCount = resultMap.getResult(ResultType.RELEVANT)
        val timeoutCount = resultMap.getResult(ResultType.TIMEOUT)
        console.logInfo("relevant: $relevantCount timeout: $timeoutCount")
        val skipFetch = isExpectedFail(resultMap) || lead.url.isFile()
        val result = if (!skipFetch) { web.fetch(lead.url, true) } else { null }

        val page = result?.let { pageReader.read(lead, it) }
        val resultType = determineResultType(skipFetch, result, page)
        if (resultType != ResultType.TIMEOUT && !skipFetch)
            logResult(result, lead.url)

        // parse strategies
        val fetch = FetchInfo(
            lead = lead,
            source = Source(
                url = page?.pageUrl ?: lead.url,
                leadTitle = lead.feedHeadline,
                attemptedAt = Clock.System.now(),
                type = page?.type ?: SourceType.UNKNOWN
            ),
            page = page,
            resultType = resultType
        )
        console.logDebug("found SourceType ${fetch.source.type}")
        // todo: if it is a news article, save a video of the endpoint

        if (page != null) {
            val md = fetch.toMarkdown()
            md?.cacheResource(page.pageUrl.domain, "md")
        }

        return fetch
    }

    private fun determineResultType(skipFetch: Boolean, result: WebResult?, page: PageInfo?): ResultType {
        if (skipFetch || result == null) return ResultType.SKIPPED
        if (result.timeout) return ResultType.TIMEOUT
        if (result.status in 400..499) return ResultType.UNAUTHORIZED
        return if (page?.type == SourceType.ARTICLE) ResultType.RELEVANT
        else ResultType.IRRELEVANT
    }

    private fun logResult(result: WebResult?, url: Url) {
        if (result == null || !result.isSuccess()) {
            console.logWarning("crawl fail (${result?.status}): $url")
            result?.screenshot?.cacheResource(url.domain, "png", "nav_fail")
        }
        result?.screenshot?.cacheResource(url.domain, "png")
        result?.doc?.html?.cacheResource(url.domain, "html", "content")
    }

    private fun isExpectedFail(resultMap: Map<ResultType, Int>?): Boolean {
        if (resultMap == null) return false
        val accessCount = resultMap.getResult(ResultType.RELEVANT) + resultMap.getResult(ResultType.IRRELEVANT)
        if (accessCount > 0) return false
        if (resultMap.getResult(ResultType.BOT_DETECT) > 0) return true
        return resultMap.getResult(ResultType.TIMEOUT) < 5
    }
}

private fun Map<ResultType, Int>.getResult(resultType: ResultType) = this[resultType] ?: 0

