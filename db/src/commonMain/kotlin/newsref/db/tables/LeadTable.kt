package newsref.db.tables

import kotlinx.datetime.*
import newsref.db.globalConsole
import newsref.db.model.FetchResult
import newsref.db.model.FetchStrategy
import newsref.db.utils.*
import newsref.db.utils.toCheckedFromTrusted
import newsref.db.core.CheckedUrl
import newsref.db.model.Lead
import newsref.db.model.LeadInfo
import newsref.db.model.LeadJob
import newsref.db.model.LeadResult
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlin.collections.plus

private val console = globalConsole.getHandle("LeadTable")

internal object LeadTable : LongIdTable("lead") {
	val url = text("url").uniqueIndex()
	val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
	val sourceId = reference("source_id", PageTable, ReferenceOption.SET_NULL).nullable().index()
}

internal class LeadRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : EntityClass<Long, LeadRow>(LeadTable)

	var host by HostRow referencedOn LeadTable.hostId
	var source by SourceRow optionalReferencedOn LeadTable.sourceId
	var url by LeadTable.url

	val results by LeadResultRow referrersOn LeadResultTable.leadId
	val links by LinkRow optionalReferrersOn LinkTable.leadId
}

internal fun LeadRow.toModel() = Lead(
	id = this.id.value,
	hostId = this.host.id.value,
	sourceId = this.source?.id?.value,
	url = this.url.toCheckedFromTrusted(),
)

internal fun LeadRow.fromModel(lead: Lead, hostRow: HostRow, sourceRow: SourceRow? = null) {
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

internal fun LeadResultRow.toModel() = LeadResult(
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

internal fun LeadResultRow.fromModel(leadResult: LeadResult, leadRow: LeadRow) {
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
	val feedPosition = integer("feed_position").nullable()
}

internal class LeadJobRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : EntityClass<Long, LeadJobRow>(LeadJobTable)

	var lead by LeadRow referencedOn LeadJobTable.leadId
	var feed by FeedRow optionalReferencedOn LeadJobTable.feedId
	var headline by LeadJobTable.headline
	var isExternal by LeadJobTable.isExternal
	var freshAt by LeadJobTable.freshAt
	var feedPosition by LeadJobTable.feedPosition
}

internal fun LeadJobRow.toModel() = LeadJob(
	id = this.id.value,
	leadId = this.lead.id.value,
	feedId = this.feed?.id?.value,
	headline = this.headline,
	isExternal = this.isExternal,
	freshAt = this.freshAt?.toInstant(UtcOffset.ZERO),
	feedPosition = this.feedPosition,
)

internal fun ResultRow.toLeadJob() = LeadJob(
	id = this[LeadJobTable.id].value,
	leadId = this[LeadJobTable.leadId].value,
	feedId = this[LeadJobTable.feedId]?.value,
	headline = this[LeadJobTable.headline],
	isExternal = this[LeadJobTable.isExternal],
	freshAt = this[LeadJobTable.freshAt]?.toInstant(UtcOffset.ZERO),
	feedPosition = this[LeadJobTable.feedPosition],
)

internal fun LeadJobRow.fromModel(leadJob: LeadJob, leadRow: LeadRow, feedRow: FeedRow?) {
	lead = leadRow
	feed = feedRow ?: leadJob.feedId?.let { FeedRow[it] }
	headline = leadJob.headline
	isExternal = leadJob.isExternal
	freshAt = leadJob.freshAt?.toLocalDateTimeUtc()
	feedPosition = leadJob.feedPosition
}

// lead info
internal val leadInfoColumns = listOf(
	LeadTable.id,
	LeadTable.url,
	LeadTable.sourceId,
	LeadTable.hostId,
	LeadJobTable.id,
	LeadJobTable.headline,
	LeadJobTable.feedPosition,
	LeadJobTable.isExternal,
	LeadJobTable.freshAt,
)

internal val linkCountAlias get() = LinkTable.leadId.count().alias("linkCount")
internal val leadInfoJoin get() = LeadTable.leftJoin(LinkTable).leftJoin(LeadJobTable)
	.select(leadInfoColumns + linkCountAlias)

internal fun ResultRow.toLeadInfo() = LeadInfo(
	id = this[LeadTable.id].value,
	url = this[LeadTable.url].toCheckedFromTrusted(),
	targetId = this[LeadTable.sourceId]?.value,
	hostId = this[LeadTable.hostId].value,
	feedHeadline = this.getOrNull(LeadJobTable.headline),
	feedPosition = this.getOrNull(LeadJobTable.feedPosition),
	lastAttemptAt = this.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO),
	isExternal = this.getOrNull(LeadJobTable.isExternal) ?: true,
	freshAt = this.getOrNull(LeadJobTable.freshAt)?.toInstant(UtcOffset.ZERO),
	linkCount = this.getOrNull(linkCountAlias)?.toInt() ?: 0,
)

internal fun Query.toLeadInfos() = this.groupBy(*leadInfoColumns.toTypedArray()).map { it.toLeadInfo() }