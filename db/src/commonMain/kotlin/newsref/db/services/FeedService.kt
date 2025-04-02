package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.tables.FeedPageTable
import newsref.db.tables.FeedTable
import newsref.db.tables.HostTable
import newsref.db.tables.NoteTable
import newsref.db.tables.PageTable
import newsref.db.tables.pageInfoColumns
import newsref.db.tables.toFeed
import newsref.db.tables.toPageInfo
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.model.Feed
import newsref.db.model.FeedPage
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.or

class FeedService : DbService() {
    suspend fun readAll(includeDisabled: Boolean = true): List<Feed> = dbQuery {
        FeedTable.selectAll().apply {
            when (includeDisabled) {
                true -> this
                false -> this.where { FeedTable.disabled.eq(false) }
            }
        }.map { it.toFeed() }
    }

    suspend fun readScheduled(): List<Feed> = dbQuery {
        val time = Clock.System.now().toLocalDateTimeUtc()
        FeedTable.selectAll()
            .where { FeedTable.checkAt.less(time) and FeedTable.disabled.eq(false) }
            .map { it.toFeed() }
    }

    suspend fun read(feedId: Int) = dbQuery {
        FeedTable.selectAll().where { FeedTable.id eq feedId }.map { it.toFeed() }.firstOrNull()
    }

    suspend fun update(feed: Feed) = dbQuery {
        FeedTable.update({ FeedTable.id eq feed.id }) {
            it[url] = feed.url.toString()
            it[selector] = feed.selector
            it[disabled] = feed.disabled
            it[external] = feed.external
            it[trackPosition] = feed.trackPosition
            it[debug] = feed.debug
            it[note] = feed.note
            it[checkAt] = feed.checkAt.toLocalDateTimeUtc()
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

    suspend fun updateFromCheck(
        feed: Feed,
        checkAt: Instant,
        linkCount: Int,
        feedPages: List<FeedPage>
    ) = dbQuery {
        FeedTable.update({ FeedTable.id eq feed.id }) {
            it[FeedTable.linkCount] = linkCount
            it[FeedTable.checkAt] = checkAt.toLocalDateTimeUtc()
        }

        if (!feed.trackPosition) return@dbQuery

        FeedPageTable.deleteWhere { FeedPageTable.feedId eq feedId }
        FeedPageTable.batchInsert(feedPages) {
            this[FeedPageTable.feedId] = feed.id
            this[FeedPageTable.pageId] = it.pageId
            this[FeedPageTable.position] = it.position
        }

        if (!feed.external) {
            for (feedSource in feedPages) {
                PageTable.update({
                    PageTable.id.eq(feedSource.pageId) and
                            (PageTable.feedPosition.isNull() or PageTable.feedPosition.greater(feedSource.position))
                }) {
                    it[PageTable.feedPosition] = feedSource.position
                }
            }
        }
    }

    suspend fun readFeedSources(feedId: Int, limit: Int = 100) = dbQuery {
        FeedPageTable.leftJoin(PageTable).leftJoin(HostTable).leftJoin(NoteTable)
            .select(pageInfoColumns + FeedPageTable.position)
            .where { FeedPageTable.feedId.eq(feedId) }
            .orderBy(FeedPageTable.position, SortOrder.ASC)
            .limit(limit)
            .map { Pair(it[FeedPageTable.position], it.toPageInfo()) }
    }
}