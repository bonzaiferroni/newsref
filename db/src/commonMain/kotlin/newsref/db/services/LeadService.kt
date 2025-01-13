package newsref.db.services

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.db.utils.sameUrl
import newsref.db.utils.toCheckedFromTrusted
import newsref.model.core.CheckedUrl
import newsref.model.data.LeadJob
import newsref.model.data.LeadInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull

private val console = globalConsole.getHandle("LeadService")

class LeadService : DbService() {

	suspend fun getOpenLeads(limit: Int = 20000) = dbQuery {
		val linkCountAlias = LinkTable.leadId.count().alias("linkCount")
		LeadTable.leftJoin(LinkTable).leftJoin(LeadJobTable)
			.select(leadInfoColumns + linkCountAlias)
			.where(LeadTable.sourceId.isNull())
			.orderBy(linkCountAlias, SortOrder.DESC)
			.orderBy(LeadJobTable.isExternal, SortOrder.DESC)
			.orderBy(LeadJobTable.freshAt, SortOrder.DESC_NULLS_LAST)
			.limit(limit)
			.groupBy(*leadInfoColumns.toTypedArray())
			// .also { println(it.prepareSQL(QueryBuilder(false))) }
			.map { row ->
				LeadInfo(
					id = row[LeadTable.id].value,
					url = row[LeadTable.url].toCheckedFromTrusted(),
					targetId = row[LeadTable.sourceId]?.value,
					hostId = row[LeadTable.hostId].value,
					feedHeadline = row.getOrNull(LeadJobTable.headline),
					lastAttemptAt = row.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO),
					isExternal = row.getOrNull(LeadJobTable.isExternal) ?: true,
					freshAt = row.getOrNull(LeadJobTable.freshAt)?.toInstant(UtcOffset.ZERO),
					linkCount = row.getOrNull(linkCountAlias)?.toInt() ?: 0
				)
			}
	}

	suspend fun createOrLinkLead(url: CheckedUrl, leadJob: LeadJob?, createIfFresh: Boolean) = dbQuery {
		var leadRow = LeadRow.find(LeadTable.url.sameUrl(url)).firstOrNull()
		if (leadRow != null) {
			val affirmed = LinkRow.setLeadOnSameLinks(url, leadRow)
			return@dbQuery if (affirmed) CreateLeadResult.AFFIRMED else CreateLeadResult.IRRELEVANT
		}
		if (!createIfFresh) return@dbQuery CreateLeadResult.IRRELEVANT

		leadRow = LeadRow.createOrUpdateAndLink(url)

		leadJob?.let { job ->
			val feedRow = job.feedId?.let { FeedRow[it] }
			LeadJobRow.new { fromData(leadJob, leadRow, feedRow) }
		}
		CreateLeadResult.CREATED // return
	}

	suspend fun getResultsByHost(hostId: Int, limit: Int) = dbQuery {
		LeadResultRow.getHostResults(hostId, limit)
	}

	suspend fun getLeadsFromFeed(feedId: Int) = dbQuery {
		LeadRow.find { LeadJobTable.feedId eq feedId }.map { it.toData() }
	}
}

class LeadExistsException(url: CheckedUrl) : IllegalArgumentException("Lead already exists: $url")

// lead info
internal val leadInfoColumns = listOf(
	LeadTable.id,
	LeadTable.url,
	LeadTable.sourceId,
	LeadTable.hostId,
	LeadJobTable.headline,
	LeadJobTable.isExternal,
	LeadJobTable.freshAt,
)

internal fun Query.wrapLeadInfo() = this.map { row ->
	LeadInfo(
		id = row[LeadTable.id].value,
		url = row[LeadTable.url].toCheckedFromTrusted(),
		targetId = row[LeadTable.sourceId]?.value,
		hostId = row[LeadTable.hostId].value,
		feedHeadline = row.getOrNull(LeadJobTable.headline),
		lastAttemptAt = row.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO),
		isExternal = row[LeadJobTable.isExternal],
		freshAt = row.getOrNull(LeadJobTable.freshAt)?.toInstant(UtcOffset.ZERO),
		linkCount = row[LinkTable.leadId.count()].toInt()
	)
}

enum class CreateLeadResult {
	CREATED,
	AFFIRMED,
	ERROR,
	IRRELEVANT
}