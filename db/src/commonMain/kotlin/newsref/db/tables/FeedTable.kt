package newsref.db.tables

import newsref.db.utils.toInstantUtc
import newsref.db.core.toUrl
import newsref.db.model.Feed
import newsref.db.model.FeedPage
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object FeedTable : IntIdTable("feed") {
    val url = text("url")
    val selector = text("selector").nullable()
    val external = bool("external").default(false)
    val trackPosition = bool("track_position").default(false)
    val linkCount = integer("link_count").default(0)
    val debug = bool("debug").default(false)
    val disabled = bool("disabled").default(false)
    val note = text("note").default("")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val checkAt = datetime("check_at").defaultExpression(CurrentDateTime)
}

fun ResultRow.toFeed() = Feed(
    id = this[FeedTable.id].value,
    url = this[FeedTable.url].toUrl(),
    selector = this[FeedTable.selector],
    external = this[FeedTable.external],
    trackPosition = this[FeedTable.trackPosition],
    linkCount = this[FeedTable.linkCount],
    debug = this[FeedTable.debug],
    disabled = this[FeedTable.disabled],
    note = this[FeedTable.note],
    createdAt = this[FeedTable.createdAt].toInstantUtc(),
    checkAt = this[FeedTable.checkAt].toInstantUtc()
)

object FeedPageTable : LongIdTable("feed_page") {
    val feedId = reference("feed_id", FeedTable, onDelete = ReferenceOption.CASCADE)
    val pageId = reference("page_id", PageTable, onDelete = ReferenceOption.CASCADE)
    val position = integer("position")
}

fun ResultRow.toFeedPosition() = FeedPage(
    id = this[FeedPageTable.id].value,
    feedId = this[FeedPageTable.feedId].value,
    pageId = this[FeedPageTable.pageId].value,
    position = this[FeedPageTable.position]
)