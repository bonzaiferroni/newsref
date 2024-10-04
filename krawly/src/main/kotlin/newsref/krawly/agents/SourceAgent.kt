package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.isMaybeArticle
import newsref.model.data.Lead
import newsref.model.data.Source
import newsref.model.data.SourceType
import newsref.model.dto.SourceInfo

class SourceAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val documentAgent: DocumentAgent = DocumentAgent(web, outletAgent),
    private val sourceService: SourceService = SourceService()
) {
    suspend fun followLead(lead: Lead): SourceInfo {
        val outlet = outletAgent.getOutlet(lead.url)                            // <- OutletAgent
        val docInfo = if (lead.url.isMaybeArticle())
            documentAgent.readDoc(lead, outlet) else null

        val info = SourceInfo(
            leadUrl = lead.url,
            source = Source(
                url = docInfo?.docUrl ?: lead.url,
                leadTitle = lead.headline,
                attemptedAt = Clock.System.now(),
                type = docInfo?.type ?: SourceType.UNKNOWN
            ),
            document = docInfo,
        )
        println("SourceAgent: found SourceType ${info.source.type}")

        val sourceId = sourceService.consume(
            info,
            info.document?.outletId ?: outlet.id
        )                                                                       //    SourceService ->

        return info.copy(id = sourceId)
    }
}

