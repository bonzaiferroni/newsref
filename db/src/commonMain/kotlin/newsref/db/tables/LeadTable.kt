package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import newsref.model.data.Lead
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object LeadTable: LongIdTable("lead") {
    val targetId = reference("target_id", SourceTable).nullable()
    val feedId = reference("feed_id", FeedTable).nullable()
    val url = text("url")
    val headline = text("headline").nullable()
    val attemptCount = integer("attempt_count")
    val attemptedAt = datetime("attempted_at").nullable()
}

class LeadRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, LeadRow>(LeadTable)

    var target by SourceRow optionalReferencedOn LeadTable.targetId
    var feed by FeedRow optionalReferencedOn LeadTable.feedId

    var url by LeadTable.url
    var headline by LeadTable.headline
    var attemptCount by LeadTable.attemptCount
    var attemptedAt by LeadTable.attemptedAt
}

fun LeadRow.toData() = Lead(
    id = this.id.value,
    sourceId = this.target?.id?.value,
    feedId = this.feed?.id?.value,
    url = this.url,
    headline = this.headline,
    attemptCount = this.attemptCount,
    attemptedAt = this.attemptedAt?.toInstant(UtcOffset.ZERO)
)

fun LeadRow.fromData(lead: Lead, sourceRow: SourceRow? = null, feedRow: FeedRow? = null) {
    target = sourceRow
    feed = feedRow
    url = lead.url
    headline = lead.headline
    attemptCount = lead.attemptCount
    attemptedAt = lead.attemptedAt?.toLocalDateTime(TimeZone.UTC)
}