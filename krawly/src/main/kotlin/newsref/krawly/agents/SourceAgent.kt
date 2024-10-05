package newsref.krawly.agents

import it.skrape.selects.Doc
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.SourceService
import newsref.krawly.utils.isMaybeArticle
import newsref.model.data.LeadJob
import newsref.model.data.Source
import newsref.model.data.SourceType
import newsref.model.dto.SourceInfo

class SourceAgent(
    private val outletAgent: OutletAgent,
    private val documentAgent: DocumentAgent = DocumentAgent(outletAgent),
    private val sourceService: SourceService = SourceService()
) {
    private val console = globalConsole.getHandle("SourceAgent")

    suspend fun read(job: LeadJob, doc: Doc?): SourceInfo {
        val outlet = outletAgent.getOutlet(job.url)                            // <- OutletAgent
        val docInfo = if (doc != null && job.url.isMaybeArticle())
            documentAgent.readDoc(job, outlet, doc) else null

        val info = SourceInfo(
            leadUrl = job.url,
            source = Source(
                url = docInfo?.docUrl ?: job.url,
                leadTitle = job.headline,
                attemptedAt = Clock.System.now(),
                type = docInfo?.type ?: SourceType.UNKNOWN
            ),
            document = docInfo,
        )
        console.logInfo("SourceAgent", "found SourceType ${info.source.type}")

        val sourceId = sourceService.consume(
            info,
            info.document?.outletId ?: outlet.id
        )                                                                       //    SourceService ->

        return info.copy(id = sourceId)
    }
}

