package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.services.SourceService
import newsref.db.utils.cacheResource
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.data.Lead
import newsref.model.data.Source
import newsref.model.dto.SourceInfo

class SourceAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val documentAgent: DocumentAgent = DocumentAgent(outletAgent),
    private val sourceService: SourceService = SourceService()
) {
    suspend fun followLead(lead: Lead): SourceInfo? {
        val result = web.crawlPage(lead.url, true) ?: return null               // <- Web
        if (!result.isSuccess() && lead.attemptCount < MAX_ATTEMPTS) {
            result.screenshot?.cacheResource(lead.url, "png", "nav_fail")
            return null
        }
        result.screenshot?.cacheResource(lead.url, "png")
        result.doc?.html?.cacheResource(lead.url, "html", "content")
        val outlet = outletAgent.getOutlet(lead.url) ?: return null             // <- OutletAgent

        val docInfo = result.doc?.let {
            documentAgent.readDoc(lead, it, outlet)                             // <- DocumentAgent ->
        }
        if (docInfo != null && docInfo.contents.isNotEmpty()) {
            val md = docInfo.toMarkdown()
            md.cacheResource(lead.url, "md")
        }

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

const val MAX_ATTEMPTS = 5