package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.FeedRow
import newsref.db.tables.FeedTable
import newsref.db.tables.toData
import newsref.db.tables.toFeed
import newsref.model.data.Feed
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class FeedService : DbService() {
    suspend fun readAll(): List<Feed> = dbQuery {
        FeedRow.all().map { it.toData() }
    }

    suspend fun read(feedId: Int) = dbQuery {
        FeedTable.selectAll().where { FeedTable.id eq feedId }.map { it.toFeed() }.firstOrNull()
    }

    suspend fun update(feed: Feed) = dbQuery {
        FeedTable.update({ FeedTable.id eq feed.id }) {
            it[url] = feed.url.toString()
            it[selector] = feed.selector
            it[external] = feed.external
            it[trackPosition] = feed.trackPosition
        }
    }

    suspend fun create(feed: Feed) = dbQuery {
        val id = FeedTable.insertAndGetId {
            it[url] = feed.url.toString()
            it[selector] = feed.selector
            it[external] = feed.external
            it[trackPosition] = feed.trackPosition
        }
        id.value
    }

    suspend fun delete(feedId: Int) = dbQuery {
        FeedTable.deleteWhere { id eq feedId }
    }
}