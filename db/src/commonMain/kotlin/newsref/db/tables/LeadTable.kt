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
    val hostId = reference("outlet_id", HostTable)
    val targetId = reference("target_id", SourceTable).nullable()
}

internal object LeadResultTable : LongIdTable("lead_result") {
    val leadId = reference("lead_id", LeadTable)
    val result = enumeration("result", ResultType::class)
    val attemptedAt = datetime("attempted_at")
    val resultCount = result.count().castTo(IntegerColumnType())
}

internal class LeadRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, LeadRow>(LeadTable)
    var host by HostRow referencedOn LeadTable.hostId
    var target by SourceRow optionalReferencedOn LeadTable.targetId
    var url by LeadTable.url

    val results by LeadResultRow referrersOn LeadResultTable.leadId
}

internal class LeadResultRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, LeadResultRow>(LeadResultTable)
    var lead by LeadRow referencedOn LeadResultTable.leadId
    var result by LeadResultTable.result
    var attemptedAt by LeadResultTable.attemptedAt
}

internal val leadInfoColumns = listOf(
    LeadTable.id,
    LeadTable.url,
    LeadTable.targetId,
    LeadTable.hostId,
    FeedJobTable.headline, // attemptCount
    // LeadResultTable.leadId.count()
)

internal fun Query.wrapLeadInfo() = this.map { row ->
    LeadInfo(
        id = row[LeadTable.id].value,
        url = row[LeadTable.url].toCheckedFromDb(),
        targetId = row[LeadTable.targetId]?.value,
        hostId = row[LeadTable.hostId].value, // Use getOrNull for nullable values
        feedHeadline = row.getOrNull(FeedJobTable.headline),       // Safely get the headline
        attemptCount = row[LeadResultTable.leadId.count()].toInt(), // count() usually returns 0 for nulls, but verify
        lastAttemptAt = row.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO) // Handle nullable datetime
    )
}

internal fun LeadRow.toData() = Lead(
    id = this.id.value,
    hostId = this.host.id.value,
    targetId = this.target?.id?.value,
    url = this.url.toCheckedFromDb(),
)

internal fun LeadResultRow.toData() = LeadResult(
    id = this.id.value,
    leadId = this.lead.id.value,
    result = this.result,
    attemptedAt = this.attemptedAt.toInstant(UtcOffset.ZERO)
)

internal fun LeadRow.fromData(lead: Lead, hostRow: HostRow, sourceRow: SourceRow? = null) {
    host = hostRow
    target = sourceRow
    url = lead.url.toString()
}

internal fun LeadResultRow.fromData(leadResult: LeadResult, leadRow: LeadRow) {
    lead = leadRow
    result = leadResult.result
    attemptedAt = leadResult.attemptedAt.toLocalDateTimeUTC()
}

internal fun LeadRow.Companion.leadExists(checkedUrl: CheckedUrl): Boolean {
    val list = mutableListOf(checkedUrl.toString().lowercase())
    if (checkedUrl.domain.startsWith("www."))
        list.add(checkedUrl.toString().replaceFirst("www.", "").lowercase())
    return this.find { LeadTable.url.lowerCase() inList list }.any()
}

internal fun LeadRow.Companion.getOutletResults(outletId: Int, since: Duration): Map<ResultType, Int> {
    val time = (Clock.System.now() - since).toLocalDateTimeUTC()
    // println("Filtered time: $time")
    // println(query.prepareSQL(QueryBuilder(false)))
    return LeadTable.leftJoin(LeadResultTable).select(LeadResultTable.resultCount, LeadResultTable.result).where {
        (LeadTable.hostId eq outletId) and (LeadResultTable.attemptedAt greaterEq time)
    }.groupBy(LeadResultTable.result).associate { resultRow ->
        resultRow[LeadResultTable.result] to resultRow[LeadResultTable.resultCount]
    }
}