package newsref.db.tables

import kotlinx.datetime.LocalDateTime
import newsref.db.utils.toInstantUtc
import newsref.model.core.toUrl
import newsref.model.data.Feed
import newsref.model.data.FeedSource
import newsref.model.data.LeadJob
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.QueryBuilder
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

class FeedRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, FeedRow>(FeedTable)

    var url by FeedTable.url
    var selector by FeedTable.selector
    var external by FeedTable.external
    var trackPosition by FeedTable.trackPosition
    var linkCount by FeedTable.linkCount
    var debug by FeedTable.debug
    var disabled by FeedTable.disabled
    var note by FeedTable.note
    var createdAt by FeedTable.createdAt
    var checkAt by FeedTable.checkAt
}

fun FeedRow.toData() = Feed(
    id = this.id.value,
    url = this.url.toUrl(),
    selector = this.selector,
    external = this.external,
    trackPosition = this.trackPosition,
    linkCount = this.linkCount,
    debug = this.debug,
    disabled = this.disabled,
    note = this.note,
    createdAt = this.createdAt.toInstantUtc(),
    checkAt = this.checkAt.toInstantUtc()
)

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

fun FeedRow.fromData(feed: Feed) {
    url = feed.url.toString()
    selector = feed.selector
    external = feed.external
    trackPosition = feed.trackPosition
}

object FeedSourceTable : LongIdTable("feed_source") {
    val feedId = reference("feed_id", FeedTable, onDelete = ReferenceOption.CASCADE)
    val sourceId = reference("source_id", SourceTable, onDelete = ReferenceOption.CASCADE)
    val position = integer("position")
}

fun ResultRow.toFeedPosition() = FeedSource(
    id = this[FeedSourceTable.id].value,
    feedId = this[FeedSourceTable.feedId].value,
    sourceId = this[FeedSourceTable.sourceId].value,
    position = this[FeedSourceTable.position]
)