package newsref.db.tables

import kotlinx.datetime.*
import newsref.db.utils.toCheckedFromDb
import newsref.db.utils.toLocalDateTimeUTC
import newsref.model.core.CheckedUrl
import newsref.model.data.*
import newsref.model.data.Lead
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlin.time.Duration

internal object LeadTable: LongIdTable("lead") {
    val url = text("url").uniqueIndex()
    val targetId = reference("target_id", SourceTable).nullable()
}

internal object LeadResultTable : LongIdTable("lead_result") {
    val leadId = reference("lead_id", LeadTable)
    val outletId = reference("outlet_id", OutletTable)
    val result = enumeration("result", ResultType::class)
    val attemptedAt = datetime("attempted_at")
    val resultCount = result.count().castTo(IntegerColumnType())
}

internal object LeadJobTable: LongIdTable("lead_job") {
    val leadId = reference("lead_id", LeadTable)
    val feedId = reference("feed_id", FeedTable).nullable()
    val headline = text("headline").nullable()
}

internal class LeadRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, LeadRow>(LeadTable)
    var target by SourceRow optionalReferencedOn LeadTable.targetId
    var url by LeadTable.url

    val results by LeadResultRow referrersOn LeadResultTable.leadId
}

internal class LeadJobRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, LeadJobRow>(LeadJobTable)
    var lead by LeadRow referencedOn LeadJobTable.leadId
    var feed by FeedRow optionalReferencedOn LeadJobTable.feedId
    var headline by LeadJobTable.headline
}

internal class LeadResultRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, LeadResultRow>(LeadResultTable)
    var lead by LeadRow referencedOn LeadResultTable.leadId
    var outlet by OutletRow referencedOn LeadResultTable.outletId
    var result by LeadResultTable.result
    var attemptedAt by LeadResultTable.attemptedAt
}

internal val leadInfoColumns = listOf(
    LeadTable.id,
    LeadTable.url,
    LeadTable.targetId,
    LeadResultTable.outletId,
    LeadJobTable.headline, // attemptCount
    // LeadResultTable.leadId.count()
)

internal fun Query.wrapLeadInfo() = this.map { row ->
    LeadInfo(
        id = row[LeadTable.id].value,
        url = row[LeadTable.url].toCheckedFromDb(),
        targetId = row[LeadTable.targetId]?.value,
        outletId = row.getOrNull(LeadResultTable.outletId)?.value, // Use getOrNull for nullable values
        feedHeadline = row.getOrNull(LeadJobTable.headline),       // Safely get the headline
        attemptCount = row[LeadResultTable.leadId.count()].toInt(), // count() usually returns 0 for nulls, but verify
        lastAttemptAt = row.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO) // Handle nullable datetime
    )
}

internal fun LeadRow.toData() = Lead(
    id = this.id.value,
    targetId = this.target?.id?.value,
    url = this.url.toCheckedFromDb(),
)

internal fun LeadResultRow.toData() = LeadResult(
    id = this.id.value,
    leadId = this.lead.id.value,
    outletId = this.outlet.id.value,
    result = this.result,
    attemptedAt = this.attemptedAt.toInstant(UtcOffset.ZERO)
)

internal fun LeadJobRow.toData() = LeadJob(
    id = this.id.value,
    leadId = this.lead.id.value,
    feedId = this.feed?.id?.value,
    headline = this.headline,
)

internal fun LeadRow.newFromData(lead: Lead, sourceRow: SourceRow? = null) {
    target = sourceRow
    url = lead.url.toString()
}

internal fun LeadJobRow.newFromData(leadJob: LeadJob, leadRow: LeadRow, feedRow: FeedRow? = null) {
    lead = leadRow
    feed = feedRow
    headline = leadJob.headline
}

internal fun LeadRow.Companion.leadExists(checkedUrl: CheckedUrl): Boolean {
    val list = mutableListOf(checkedUrl.toString().lowercase())
    if (checkedUrl.host.startsWith("www."))
        list.add(checkedUrl.toString().replaceFirst("www.", "").lowercase())
    return this.find { LeadTable.url.lowerCase() inList list }.any()
}

internal fun LeadResultRow.Companion.getOutletResults(outletId: Int, since: Duration): Map<ResultType, Int> {
    val time = (Clock.System.now() - since).toLocalDateTimeUTC()
    // println("Filtered time: $time")
    // println(query.prepareSQL(QueryBuilder(false)))
    return LeadResultTable.select(LeadResultTable.resultCount, LeadResultTable.result).where {
        (LeadResultTable.outletId eq outletId) and (LeadResultTable.attemptedAt greaterEq time)
    }.groupBy(LeadResultTable.result).associate { resultRow ->
        resultRow[LeadResultTable.result] to resultRow[LeadResultTable.resultCount]
    }
}