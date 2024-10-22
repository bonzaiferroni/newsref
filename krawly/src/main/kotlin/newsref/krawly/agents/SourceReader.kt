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
        val resultMap = leadService.getResultsByHost(lead.hostId, 1.hours)

        val skipFetch = isExpectedFail(resultMap) || lead.url.isFile()
        val result = if (!skipFetch) { web.fetch(lead.url, true) } else { null }

        val page = result?.let { pageReader.read(lead, it) }
        val resultType = determineResultType(skipFetch, result, page)
        if (resultType != ResultType.TIMEOUT && !skipFetch)
            cacheResult(result, lead.url)

        // parse strategies
        val fetch = FetchInfo(
            lead = lead,
            source = Source(
                url = page?.pageUrl ?: lead.url,
                leadTitle = lead.feedHeadline,
                seenAt = Clock.System.now(),
                type = page?.type ?: SourceType.UNKNOWN
            ),
            page = page,
            resultType = resultType,
            resultMap = resultMap.toMutableMap().also { it[resultType] = it.getResult(resultType) + 1 },
            statusOk = result?.isSuccess()
        )
        // todo: if it is a news article, save a video of the endpoint

        if (page != null) {
            val md = fetch.toMarkdown()
            md?.cacheResource(page.pageUrl.domain, "md")
        }

        return fetch
    }

    private fun determineResultType(skipFetch: Boolean, result: WebResult?, page: PageInfo?): ResultType {
        if (skipFetch) return ResultType.SKIPPED
        if (result == null) return ResultType.UNKNOWN
        if (result.timeout) return ResultType.TIMEOUT
        if (result.status in 400..499) return ResultType.UNAUTHORIZED
        // todo: support other languages
        if (page?.language?.startsWith("en") != true) return ResultType.IRRELEVANT
        if (page?.type == SourceType.ARTICLE) {
            if (page.foundNewsArticle) return ResultType.RELEVANT
            // todo: add more relevance indicators
        }
        return ResultType.IRRELEVANT
    }

    private fun cacheResult(result: WebResult?, url: Url) {
        if (result == null || !result.isSuccess()) {
            result?.screenshot?.cacheResource(url.domain, "png", "nav_fail")
        }
//        result?.screenshot?.cacheResource(url.domain, "png")
        result?.doc?.html?.cacheResource(url.domain, "html", "html")
    }

    private fun isExpectedFail(resultMap: Map<ResultType, Int>?): Boolean {
        if (resultMap == null) return false
        val accessCount = resultMap.getResult(ResultType.RELEVANT) + resultMap.getResult(ResultType.IRRELEVANT)
        if (accessCount > 0) return false
        if (resultMap.getResult(ResultType.BOT_DETECT) > 0) return true
        return resultMap.getResult(ResultType.TIMEOUT) > 5
    }
}

fun Map<ResultType, Int>.getResult(resultType: ResultType) = this[resultType] ?: 0

