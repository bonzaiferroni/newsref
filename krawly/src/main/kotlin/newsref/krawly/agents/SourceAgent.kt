package newsref.krawly.agents

import com.eygraber.uri.Url
import kotlinx.datetime.Clock
import newsref.db.services.SourceService
import newsref.db.utils.cacheResource
import newsref.db.utils.toUrlOrNull
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.data.Lead
import newsref.model.data.Source
import newsref.model.data.SourceType
import newsref.model.dto.SourceInfo

class SourceAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val sourceService: SourceService = SourceService()
) {
    suspend fun follow(lead: Lead): SourceInfo? {
        val result = web.crawlPage(lead.url, true) ?: return null
        if (!result.isSuccess() && lead.attemptCount < MAX_ATTEMPTS) {
            result.screenshot?.cacheResource(lead.url, "png", "nav_fail")
            return null
        }
        result.screenshot?.cacheResource(lead.url, "png")
        result.content?.cacheResource(lead.url, "html", "content")
        val document = result.content?.contentToDoc()                           // <- parse document
        val newsArticle = document?.getNewsArticle(lead.url).also { if (it != null) { print("📜 ") } }
        val sourceUrl = newsArticle?.url?.toUrlOrNull() ?: document?.readUrl()?.toUrlOrNull() ?: lead.url
        val outletName = newsArticle?.publisher?.name ?: document?.readOutletName()
        val outlet = outletAgent.findAndSetName(sourceUrl, outletName)          // <- OutletAgent ->
        val docInfo = document?.read(lead, outlet, newsArticle)                 // <- read document
        if (docInfo != null && docInfo.contents.isNotEmpty()) {
            val md = docInfo.toMarkdown()
            md.cacheResource(lead.url, "md")
        }

        val info = SourceInfo(
            leadUrl = lead.url,
            source = Source(
                url = sourceUrl,
                leadTitle = lead.headline,
                type = newsArticle?.let { SourceType.ARTICLE } ?: document?.readType() ?: SourceType.UNKNOWN,
                attemptedAt = Clock.System.now()
            ),
            document = docInfo,
        )

        val sourceId = sourceService.consume(info, outlet.id)                   //    SourceService ->

        return info.copy(id = sourceId)
    }
}

const val MAX_ATTEMPTS = 5