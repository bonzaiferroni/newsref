package newsref.krawly.agents

import it.skrape.selects.Doc
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.SourceService
import newsref.db.utils.cacheResource
import newsref.krawly.MAX_URL_ATTEMPTS
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.isMaybeArticle
import newsref.model.core.toCheckedUrl
import newsref.model.core.toUrlOrNull
import newsref.model.data.LeadJob
import newsref.model.data.Source
import newsref.model.data.SourceType
import newsref.model.dto.SourceInfo

class SourceAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val docReader: DocReader = DocReader(outletAgent),
    private val sourceService: SourceService = SourceService()
) {
    private val console = globalConsole.getHandle("SourceAgent", true)

    suspend fun read(job: LeadJob): SourceInfo? {

        val result = web.crawlPage(job.url, true)
        val doc = result?.doc
        if (doc == null || !result.isSuccess()) {
            console.logWarning("crawl fail (${result?.status}): ${job.url}")
            result?.screenshot?.cacheResource(job.url, "png", "nav_fail")

            // keep job and try a few times
            if (job.attemptCount < MAX_URL_ATTEMPTS) return null
        }
        result?.screenshot?.cacheResource(job.url, "png")
        result?.doc?.html?.cacheResource(job.url, "html", "content")

        val resultUrl = result?.url?.toUrlOrNull()
        val resultOutlet = resultUrl?.let { outletAgent.getOutlet(it) }
        val resultCheckedUrl = resultOutlet?.let { resultUrl.toString().toCheckedUrl(it) }

        val outlet = resultOutlet ?: outletAgent.getOutlet(job.url)
        val url = resultCheckedUrl ?: job.url

        // parse strategies
        val docInfo = if (doc != null && url.isMaybeArticle())
            docReader.readDoc(url, outlet, doc) else null

        val info = SourceInfo(
            leadUrl = resultCheckedUrl ?: job.url,
            source = Source(
                url = docInfo?.docUrl ?: job.url,
                leadTitle = job.headline,
                attemptedAt = Clock.System.now(),
                type = docInfo?.type ?: SourceType.UNKNOWN
            ),
            document = docInfo,
        )
        console.logDebug("found SourceType ${info.source.type}")

        val outletId = info.document?.outletId ?: outlet.id
        val sourceId = sourceService.consume(info, outletId)                                                                       //    SourceService ->

        return info.copy(id = sourceId)
    }
}

