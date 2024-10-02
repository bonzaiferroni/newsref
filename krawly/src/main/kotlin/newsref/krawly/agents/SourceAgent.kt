package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.services.SourceService
import newsref.krawly.SpiderWeb
import newsref.model.data.Lead
import newsref.model.data.Source
import newsref.model.dto.SourceInfo

class SourceAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val documentAgent: DocumentAgent = DocumentAgent(web, outletAgent),
    private val sourceService: SourceService = SourceService()
) {
    suspend fun followLead(lead: Lead): SourceInfo? {
        val outlet = outletAgent.getOutlet(lead.url) ?: return null             // <- OutletAgent

        val docInfo = documentAgent.readDoc(lead, outlet)

        val info = SourceInfo(
            leadUrl = lead.url,
            source = Source(
                url = docInfo?.docUrl ?: lead.url,
                leadTitle = lead.headline,
                attemptedAt = Clock.System.now()
            ),
            document = docInfo,
        )

        val sourceId = sourceService.consume(
            info,
            info.document?.outletId ?: outlet.id
        )                                                                       //    SourceService ->

        return info.copy(id = sourceId)
    }
}

