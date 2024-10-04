package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import newsref.db.utils.toCheckedFromDb
import newsref.model.data.LeadJob
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object LeadJobTable: LongIdTable("lead_job") {
	val url = text("url")
	val feedId = reference("feed_id", FeedTable).nullable()
	val headline = text("headline").nullable()
	val attemptCount = integer("attempt_count")
	val attemptedAt = datetime("attempted_at").nullable()
}

class LeadJobRow(id: EntityID<Long>): LongEntity(id) {
	companion object: EntityClass<Long, LeadJobRow>(LeadJobTable)
	var url by LeadJobTable.url
	var feed by FeedRow optionalReferencedOn LeadJobTable.feedId
	var headline by LeadJobTable.headline
	var attemptCount by LeadJobTable.attemptCount
	var attemptedAt by LeadJobTable.attemptedAt
}

fun LeadJobRow.toData() = LeadJob(
	id = this.id.value,
	url = this.url.toCheckedFromDb(),
	feedId = this.feed?.id?.value,
	headline = this.headline,
	attemptCount = this.attemptCount,
	attemptedAt = this.attemptedAt?.toInstant(UtcOffset.ZERO)
)

fun LeadJobRow.fromData(leadJob: LeadJob, feedRow: FeedRow? = null) {
	url = leadJob.url.toString()
	feed = feedRow
	headline = leadJob.headline
	attemptCount = leadJob.attemptCount
	attemptedAt = leadJob.attemptedAt?.toLocalDateTime(TimeZone.UTC)
}