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
    val hostId = reference("host_id", HostTable)
    val targetId = reference("target_id", SourceTable).nullable()
}

internal class LeadRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, LeadRow>(LeadTable)
    var host by HostRow referencedOn LeadTable.hostId
    var target by SourceRow optionalReferencedOn LeadTable.targetId
    var url by LeadTable.url

    val results by LeadResultRow referrersOn LeadResultTable.leadId
}

internal fun LeadRow.toData() = Lead(
    id = this.id.value,
    hostId = this.host.id.value,
    targetId = this.target?.id?.value,
    url = this.url.toCheckedFromDb(),
)

internal fun LeadRow.fromData(lead: Lead, hostRow: HostRow, sourceRow: SourceRow? = null) {
    host = hostRow
    target = sourceRow
    url = lead.url.toString()
}

internal fun LeadRow.Companion.leadExists(checkedUrl: CheckedUrl): Boolean {
    val list = mutableListOf(checkedUrl.toString().lowercase())
    if (checkedUrl.domain.startsWith("www."))
        list.add(checkedUrl.toString().replaceFirst("www.", "").lowercase())
    return this.find { LeadTable.url.lowerCase() inList list }.any()
}

internal fun LeadRow.Companion.getHostResults(hostId: Int, since: Duration): Map<ResultType, Int> {
    val time = (Clock.System.now() - since).toLocalDateTimeUTC()
    // println("Filtered time: $time")
    // println(query.prepareSQL(QueryBuilder(false)))
    return LeadTable.leftJoin(LeadResultTable).select(LeadResultTable.resultCount, LeadResultTable.result).where {
        (LeadTable.hostId eq hostId) and (LeadResultTable.attemptedAt greaterEq time)
    }.groupBy(LeadResultTable.result).associate { resultRow ->
        resultRow[LeadResultTable.result] to resultRow[LeadResultTable.resultCount]
    }
}

// lead result
internal object LeadResultTable : LongIdTable("lead_result") {
    val leadId = reference("lead_id", LeadTable)
    val result = enumeration("result", ResultType::class)
    val attemptedAt = datetime("attempted_at")
    val resultCount = result.count().castTo(IntegerColumnType())
}

internal class LeadResultRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, LeadResultRow>(LeadResultTable)
    var lead by LeadRow referencedOn LeadResultTable.leadId
    var result by LeadResultTable.result
    var attemptedAt by LeadResultTable.attemptedAt
}

internal fun LeadResultRow.toData() = LeadResult(
    id = this.id.value,
    leadId = this.lead.id.value,
    result = this.result,
    attemptedAt = this.attemptedAt.toInstant(UtcOffset.ZERO)
)

internal fun LeadResultRow.fromData(leadResult: LeadResult, leadRow: LeadRow) {
    lead = leadRow
    result = leadResult.result
    attemptedAt = leadResult.attemptedAt.toLocalDateTimeUTC()
}

// lead job
internal object LeadJobTable: LongIdTable("lead_job") {
    val feedId = reference("feed_id", FeedTable, ReferenceOption.SET_NULL).nullable()
    val leadId = reference("lead_id", LeadTable).index()
    val headline = text("headline").nullable()
    val isExternal = bool("is_external")
    val freshAt = datetime("fresh_at").nullable().index()
}

internal class LeadJobRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, LeadJobRow>(LeadJobTable)
    var lead by LeadRow referencedOn LeadJobTable.leadId
    var feed by FeedRow optionalReferencedOn LeadJobTable.feedId
    var headline by LeadJobTable.headline
    var isExternal by LeadJobTable.isExternal
    var freshAt by LeadJobTable.freshAt
}

internal fun LeadJobRow.toData() = LeadJob(
    id = this.id.value,
    leadId = this.lead.id.value,
    feedId = this.feed?.id?.value,
    headline = this.headline,
    isExternal = this.isExternal,
    freshAt = this.freshAt?.toInstant(UtcOffset.ZERO)
)

internal fun LeadJobRow.fromData(leadJob: LeadJob, leadRow: LeadRow, feedRow: FeedRow?) {
    lead = leadRow
    feed = feedRow
    headline = leadJob.headline
    isExternal = leadJob.isExternal
    freshAt = leadJob.freshAt?.toLocalDateTimeUTC()
}