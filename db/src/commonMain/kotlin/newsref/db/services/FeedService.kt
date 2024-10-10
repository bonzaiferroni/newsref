package newsref.db.services

import newsref.db.DataService
import newsref.db.tables.FeedRow
import newsref.db.tables.newFromData
import newsref.db.tables.toData
import newsref.model.data.Feed

class FeedService : DataService<Feed, Int, FeedRow>(
    FeedRow,
    { it.id },
    FeedRow::newFromData,
    FeedRow::toData
) {
    suspend fun init() = dbQuery {
        if (FeedRow.all().count() > 0) return@dbQuery
        FeedRow.new {
            this.url = System.getenv("NEWSREF_INIT_FEED_URL")
            this.selector = System.getenv("NEWSREF_INIT_FEED_SELECTOR")
        }
    }
}