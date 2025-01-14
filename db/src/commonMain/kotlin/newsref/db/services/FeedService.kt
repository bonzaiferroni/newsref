package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.FeedRow
import newsref.db.tables.FeedTable
import newsref.db.tables.toData
import newsref.db.tables.toFeed
import newsref.db.tables.toLeadJob
import newsref.model.data.Feed
import org.jetbrains.exposed.sql.selectAll

class FeedService : DbService() {
    suspend fun readAll(): List<Feed> = dbQuery {
        FeedRow.all().map { it.toData() }
    }

    suspend fun read(feedId: Int) = dbQuery {
        FeedTable.selectAll().where { FeedTable.id eq feedId }.map { it.toFeed() }.firstOrNull()
    }
}