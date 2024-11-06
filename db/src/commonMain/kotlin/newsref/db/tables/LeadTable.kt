package newsref.db.tables

import kotlinx.datetime.*
import newsref.db.globalConsole
import newsref.db.utils.*
import newsref.db.utils.toCheckedFromTrusted
import newsref.model.core.CheckedUrl
import newsref.model.data.*
import newsref.model.data.Lead
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

private val console = globalConsole.getHandle("LeadTable")

internal object LeadTable : LongIdTable("lead") {
	val url = text("url").uniqueIndex()
	val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
	val sourceId = reference("source_id", SourceTable, ReferenceOption.SET_NULL).nullable().index()
}

internal class LeadRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : EntityClass<Long, LeadRow>(LeadTable)

	var host by HostRow referencedOn LeadTable.hostId
	var source by SourceRow optionalReferencedOn LeadTable.sourceId
	var url by LeadTable.url

	val results by LeadResultRow referrersOn LeadResultTable.leadId
	val links by LinkRow optionalReferrersOn LinkTable.leadId
}

internal fun LeadRow.toData() = Lead(
	id = this.id.value,
	hostId = this.host.id.value,
	sourceId = this.source?.id?.value,
	url = this.url.toCheckedFromTrusted(),
)

internal fun LeadRow.fromData(lead: Lead, hostRow: HostRow, sourceRow: SourceRow? = null) {
	host = hostRow
	source = sourceRow
	url = lead.url.toString()
}

internal fun LeadRow.Companion.leadExists(checkedUrl: CheckedUrl): Boolean {
	return this.find { LeadTable.url.sameUrl(checkedUrl) }.any()
}

internal fun LeadResultRow.Companion.getHostResults(hostId: Int, limit: Int): List<LeadResult> {
	return LeadResultTable.leftJoin(LeadTable)
		.select(LeadResultTable.columns)
		.where { LeadTable.hostId eq hostId }
		.limit(limit)
		.wrapLeadResults()
}

internal fun LeadRow.Companion.createOrUpdateAndLink(url: CheckedUrl, source: SourceRow? = null): LeadRow {
	val hostRow = HostRow.findByCore(url.core) ?: throw IllegalArgumentException("Host missing: ${url.core}")
	val lead = LeadRow.createOrUpdate(LeadTable.url.sameUrl(url)) {
		this.url = url.href
		this.host = hostRow
		source?.let { this.source = it }
	}
	LinkRow.setLeadOnSameLinks(url, lead)
	return lead
}

// lead result
internal object LeadResultTable : LongIdTable("lead_result") {
	val leadId = reference("lead_id", LeadTable, ReferenceOption.SET_NULL)
	val result = enumeration("result", FetchResult::class)
	val attemptedAt = datetime("attempted_at")
	val strategy = enumeration("strategy", FetchStrategy::class).nullable()

	val resultCount = result.count().castTo(IntegerColumnType())
}

internal class LeadResultRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : EntityClass<Long, LeadResultRow>(LeadResultTable)

	var lead by LeadRow referencedOn LeadResultTable.leadId
	var result by LeadResultTable.result
	var attemptedAt by LeadResultTable.attemptedAt
	var strategy by LeadResultTable.strategy
}

internal fun LeadResultRow.toData() = LeadResult(
	id = this.id.value,
	leadId = this.lead.id.value,
	result = this.result,
	attemptedAt = this.attemptedAt.toInstant(UtcOffset.ZERO),
	strategy = this.strategy,
)

internal fun Query.wrapLeadResults() = this.map { row ->
	LeadResult(
		id = row[LeadResultTable.id].value,
		leadId = row[LeadResultTable.leadId].value,
		result = row[LeadResultTable.result],
		attemptedAt = row[LeadResultTable.attemptedAt].toInstant(UtcOffset.ZERO),
		strategy = row[LeadResultTable.strategy],
	)
}

internal fun LeadResultRow.fromData(leadResult: LeadResult, leadRow: LeadRow) {
	lead = leadRow
	result = leadResult.result
	attemptedAt = leadResult.attemptedAt.toLocalDateTimeUtc()
	strategy = leadResult.strategy
}

// lead job
internal object LeadJobTable : LongIdTable("lead_job") {
	val feedId = reference("feed_id", FeedTable, ReferenceOption.SET_NULL).nullable()
	val leadId = reference("lead_id", LeadTable, ReferenceOption.CASCADE).index()
	val headline = text("headline").nullable()
	val isExternal = bool("is_external")
	val freshAt = datetime("fresh_at").nullable().index()
}

internal class LeadJobRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : EntityClass<Long, LeadJobRow>(LeadJobTable)

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
	freshAt = leadJob.freshAt?.toLocalDateTimeUtc()
}