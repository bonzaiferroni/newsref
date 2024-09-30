package newsref.krawly.agents

import newsref.db.services.SourceService
import newsref.db.utils.cacheResource
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.data.Lead
import newsref.model.utils.getApexDomain

class SourceAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val sourceService: SourceService = SourceService()
) {
    suspend fun follow(lead: Lead): FollowResult? {
        val result = web.crawlPage(lead.url, true)
        if (!result.isSuccess() && lead.attemptCount < MAX_ATTEMPTS) {
            result.screenshot?.cacheResource(lead.url, "png", "nav_fail")
            return null
        }
        result.screenshot?.cacheResource(lead.url, "png")
        result.content?.cacheResource(lead.url, "html", "content")
        val document = result.content?.let { contentToDoc(it) }
        println("Reader: reading document")
        val info = document?.readFromLead(lead) ?: return null
        if (info.contents.isNotEmpty()) {
            val md = info.toMarkdown()
            md?.cacheResource(lead.url, "md")
        }

        val outlet = outletAgent.getOutlet(info.source.url)
        val sourceId = sourceService.consume(info, outlet.id)
        val host = info.source.url.getHostAndProtocol()
        return FollowResult(sourceId, info.links.map {
            if (it.url.hasHostAndProtocol()) it.url else "$host${it.url}"
        })
    }
}

data class FollowResult(
    val sourceId: Long,
    val leads: List<String>
)

const val MAX_ATTEMPTS = 5