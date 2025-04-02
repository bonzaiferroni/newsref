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
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlin.collections.plus

private val console = globalConsole.getHandle("LeadTable")

internal object LeadTable : LongIdTable("lead") {
	val url = text("url").uniqueIndex()
	val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
	val pageId = reference("page_id", PageTable, ReferenceOption.SET_NULL).nullable().index()
}

internal fun ResultRow.toLead() = Lead(
	id = this[LeadTable.id].value,
	hostId = this[LeadTable.hostId].value,
	pageId = this[LeadTable.pageId]?.value,
	url = this[LeadTable.url].toCheckedFromTrusted(),
)

internal fun LeadResultTable.getHostResults(hostId: Int, limit: Int) = LeadResultTable.leftJoin(LeadTable)
	.select(LeadResultTable.columns)
	.where { LeadTable.hostId eq hostId }
	.limit(limit)
	.wrapLeadResults()

internal fun LeadTable.createOrUpdateAndLink(url: CheckedUrl, pageId: Long? = null): Long {
	val host = HostTable.findByCore(url.core) ?: throw IllegalArgumentException("Host missing: ${url.core}")

	val leadId = this.upsert(where = {LeadTable.url.sameUrl(url)}) {
		it[this.url] = url.href
		it[hostId] = host.id
		it[this.pageId] = pageId
	}[id].value
	LinkTable.setLeadOnSameLinks(url, leadId)

	return leadId
}

// lead result
internal object LeadResultTable : LongIdTable("lead_result") {
	val leadId = reference("lead_id", LeadTable, ReferenceOption.SET_NULL)
	val result = enumeration("result", FetchResult::class)
	val attemptedAt = datetime("attempted_at")
	val strategy = enumeration("strategy", FetchStrategy::class).nullable()

	val resultCount = result.count().castTo(IntegerColumnType())
}

internal fun ResultRow.toLeadResult() = LeadResult(
	id = this[LeadResultTable.id].value,
	leadId = this[LeadResultTable.leadId].value,
	result = this[LeadResultTable.result],
	attemptedAt = this[LeadResultTable.attemptedAt].toInstantUtc(),
	strategy = this[LeadResultTable.strategy],
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

// lead job
internal object LeadJobTable : LongIdTable("lead_job") {
	val feedId = reference("feed_id", FeedTable, ReferenceOption.SET_NULL).nullable()
	val leadId = reference("lead_id", LeadTable, ReferenceOption.CASCADE).index()
	val headline = text("headline").nullable()
	val isExternal = bool("is_external")
	val freshAt = datetime("fresh_at").nullable().index()
	val feedPosition = integer("feed_position").nullable()
}

internal fun ResultRow.toLeadJob() = LeadJob(
	id = this[LeadJobTable.id].value,
	leadId = this[LeadJobTable.leadId].value,
	feedId = this[LeadJobTable.feedId]?.value,
	headline = this[LeadJobTable.headline],
	isExternal = this[LeadJobTable.isExternal],
	freshAt = this[LeadJobTable.freshAt]?.toInstant(UtcOffset.ZERO),
	feedPosition = this[LeadJobTable.feedPosition],
)

// lead info
internal val leadInfoColumns = listOf(
	LeadTable.id,
	LeadTable.url,
	LeadTable.pageId,
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
	targetId = this[LeadTable.pageId]?.value,
	hostId = this[LeadTable.hostId].value,
	feedHeadline = this.getOrNull(LeadJobTable.headline),
	feedPosition = this.getOrNull(LeadJobTable.feedPosition),
	lastAttemptAt = this.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO),
	isExternal = this.getOrNull(LeadJobTable.isExternal) ?: true,
	freshAt = this.getOrNull(LeadJobTable.freshAt)?.toInstant(UtcOffset.ZERO),
	linkCount = this.getOrNull(linkCountAlias)?.toInt() ?: 0,
)

internal fun Query.toLeadInfos() = this.groupBy(*leadInfoColumns.toTypedArray()).map { it.toLeadInfo() }