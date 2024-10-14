package newsref.db.tables

import newsref.model.core.toUrl
import newsref.model.data.Feed
import newsref.model.data.FeedJob
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

internal object FeedTable : IntIdTable("feed") {
    val url = text("url")
    val selector = text("selector")
}

class FeedRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, FeedRow>(FeedTable)

    var url by FeedTable.url
    var selector by FeedTable.selector
}

fun FeedRow.toData() = Feed(
    id = this.id.value,
    url = this.url.toUrl(),
    selector = this.selector,
)

fun FeedRow.fromData(feed: Feed) {
    url = feed.url.toString()
    selector = feed.selector
}

internal object FeedJobTable: LongIdTable("lead_job") {
    val feedId = reference("feed_id", FeedTable)
    val leadId = reference("lead_id", LeadTable)
    val headline = text("headline").nullable()
}

internal class FeedJobRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, FeedJobRow>(FeedJobTable)
    var lead by LeadRow referencedOn FeedJobTable.leadId
    var feed by FeedRow referencedOn FeedJobTable.feedId
    var headline by FeedJobTable.headline
}

internal fun FeedJobRow.toData() = FeedJob(
    id = this.id.value,
    leadId = this.lead.id.value,
    feedId = this.feed.id.value,
    headline = this.headline,
)

internal fun FeedJobRow.fromData(feedJob: FeedJob, leadRow: LeadRow, feedRow: FeedRow) {
    lead = leadRow
    feed = feedRow
    headline = feedJob.headline
}