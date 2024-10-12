package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.services.SourceService
import newsref.db.utils.cacheResource
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.WebResult
import newsref.krawly.utils.isFile
import newsref.model.core.SourceType
import newsref.model.core.Url
import newsref.model.data.LeadInfo
import newsref.model.data.ResultType
import newsref.model.data.Source
import newsref.model.dto.FetchInfo
import kotlin.time.Duration.Companion.days

class SourceAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val pageReader: PageReader = PageReader(outletAgent),
    private val sourceService: SourceService = SourceService(),
    private val leadService: LeadService = LeadService(),
) {
    private val console = globalConsole.getHandle("SourceAgent", true)

    suspend fun read(lead: LeadInfo): FetchInfo {
        val resultMap = lead.outletId.let{ leadService.getResultsByOutlet(it, 1.days) }
        val skipFetch = isExpectedFail(resultMap) && lead.url.isFile()
        val result = if (skipFetch) { web.crawlPage(lead.url, true) } else { null }

        val pageInfo = result?.let { pageReader.read(lead, it) }

        // parse strategies
        val info = FetchInfo(
            source = Source(
                url = pageInfo?.pageUrl ?: lead.url,
                leadTitle = lead.feedHeadline,
                attemptedAt = Clock.System.now(),
                type = pageInfo?.type ?: SourceType.UNKNOWN
            ),
            page = pageInfo,
        )
        console.logDebug("found SourceType ${info.source.type}")

        val sourceId = sourceService.consume(info, lead)                    //    SourceService ->
        // todo: if it is a news article, save a video of the endpoint
        // leadService.addResult(lead.id, )
        // leadService.addSource(job.leadId, sourceInfo.id)                    //    LeadService ->

        return info.copy(id = sourceId)
    }

    private fun logResult(result: WebResult?, url: Url) {
        if (result == null || !result.isSuccess()) {
            console.logWarning("crawl fail (${result?.status}): $url")
            result?.screenshot?.cacheResource(url, "png", "nav_fail")
        }
        result?.screenshot?.cacheResource(url, "png")
        result?.doc?.html?.cacheResource(url, "html", "content")
    }

    private fun isExpectedFail(resultMap: Map<ResultType, Int>?): Boolean {
        if (resultMap == null) return false
        val accessCount = resultMap.getResult(ResultType.RELEVANT) + resultMap.getResult(ResultType.IRRELEVANT)
        if (accessCount > 0) return false
        if (resultMap.getResult(ResultType.BOT_DETECT) > 0) return true
        return resultMap.getResult(ResultType.TIMEOUT) < 3
    }
}

private fun Map<ResultType, Int>.getResult(resultType: ResultType) = this[resultType] ?: 0

