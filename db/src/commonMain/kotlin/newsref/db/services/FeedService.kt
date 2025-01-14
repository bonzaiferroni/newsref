package newsref.db.services

import newsref.db.DataService
import newsref.db.DbService
import newsref.db.environment
import newsref.db.tables.FeedRow
import newsref.db.tables.FeedTable
import newsref.db.tables.fromData
import newsref.db.tables.toData
import newsref.model.data.Feed
import org.jetbrains.exposed.sql.selectAll

class FeedService : DbService() {
    suspend fun readAll(): List<Feed> = dbQuery {
        FeedRow.all().map { it.toData() }
    }
}