package newsref.krawly.agents

import newsref.db.services.FeedService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.isLikelyAd
import newsref.krawly.utils.tryGetHref
import newsref.model.core.CheckedUrl
import newsref.model.core.toCheckedUrl
import newsref.model.core.toUrlOrNull

class FeedAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val feedService: FeedService = FeedService(),
) {

    suspend fun checkFeeds(): List<FeedLead> {
        feedService.init()                                                      //    FeedService ->
        val feeds = feedService.readAll()                                       // <- FeedService
        val list = mutableListOf<FeedLead>()
        for (feed in feeds) {
            val webResult = web.crawlPage(feed.url)                             // <- Web
            if (webResult == null || !webResult.isSuccess() || webResult.doc == null) {
                println("FeedAgent: feed error: ${feed.url}")
                continue
            }
            val doc = webResult.doc                                             // <- Parse
            for (docElement in doc.findAll(feed.selector)) {
                val (headline, href) = docElement.tryGetHref() ?: continue
                val url = href.toUrlOrNull() ?: continue
                if (url.isLikelyAd()) continue
                val outlet = outletAgent.getOutlet(url)                         // <- OutletAgent ->
                val checkedUrl = href.toCheckedUrl(outlet)
                list += FeedLead(feedId = feed.id, url = checkedUrl, headline = headline)
            }
        }

        return list
    }
}

data class FeedLead(
    val feedId: Int,
    val url: CheckedUrl,
    val headline: String
)