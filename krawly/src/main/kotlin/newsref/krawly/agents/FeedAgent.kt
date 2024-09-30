package newsref.krawly.agents

import kotlinx.serialization.json.JsonNull.content
import newsref.db.services.FeedService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.contentToDoc

class FeedAgent(
    private val web: SpiderWeb,
    private val feedService: FeedService = FeedService(),
) {

    suspend fun checkFeeds(): List<FeedLead> {
        feedService.init()
        val feeds = feedService.readAll()
        val list = mutableListOf<FeedLead>()
        for (feed in feeds) {
            val webResult = web.crawlPage(feed.url)
            if (!webResult.isSuccess() || webResult.content == null) {
                println("FeedAgent: feed error: ${feed.url}")
                continue
            }
            val doc = contentToDoc(webResult.content)
            doc.findAll(feed.leadSelector).forEach { docElement ->
                val (headline, url) = docElement.eachLink.entries.firstOrNull() ?: return@forEach
                list += FeedLead(feedId = feed.id, url = url, headline = headline)
            }
        }

        return list
    }
}

data class FeedLead(
    val feedId: Int,
    val url: String,
    val headline: String
)