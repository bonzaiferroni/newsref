package newsref.krawly

import newsref.db.initDb
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.db.services.OutletService
import newsref.krawly.agents.OutletAgent

suspend fun main(args: Array<String>) {
    initDb()
    val spider = Spider(SpiderWeb())
    spider.startCrawling()
}
