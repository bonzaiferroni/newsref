package newsref.krawly.agents

import com.eygraber.uri.Url
import newsref.db.services.FeedService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.tryGetHref
import newsref.model.utils.tryParseTrustedUrl

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
                val url = outletAgent.getTrustedUrl(href, null) ?: continue     // <- OutletAgent ->
                list += FeedLead(feedId = feed.id, url = url, headline = headline)
            }
        }

        return list
    }
}

data class FeedLead(
    val feedId: Int,
    val url: Url,
    val headline: String
)