package newsref.db.services

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.sameUrl
import newsref.db.utils.toCheckedFromDb
import newsref.model.core.CheckedUrl
import newsref.model.data.LeadJob
import newsref.model.data.LeadInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.postgresql.jdbc.PgResultSet.toInt

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
                    url = row[LeadTable.url].toCheckedFromDb(),
                    targetId = row[LeadTable.sourceId]?.value,
                    hostId = row[LeadTable.hostId].value,
                    feedHeadline = row.getOrNull(LeadJobTable.headline),
                    lastAttemptAt = row.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO),
                    isExternal = row[LeadJobTable.isExternal],
                    freshAt = row.getOrNull(LeadJobTable.freshAt)?.toInstant(UtcOffset.ZERO),
                    linkCount = row.getOrNull(linkCountAlias)?.toInt() ?: 0
                )
            }
    }

    suspend fun createIfFreshLead(url: CheckedUrl, leadJob: LeadJob?) = dbQuery {
       var leadRow = LeadRow.find(LeadTable.url.sameUrl(url)).firstOrNull()
        if (leadRow != null) {
            LinkRow.setLeadOnSameLinks(url, leadRow)
            return@dbQuery null
        }

        leadRow = LeadRow.createOrUpdateAndLink(url)

        leadJob?.let { job ->
            val feedRow = job.feedId?.let { FeedRow[it] }
            LeadJobRow.new { fromData(leadJob, leadRow, feedRow) }
        }
        leadRow.toData() // return
    }

    suspend fun getResultsByHost(hostId: Int, limit: Int) = dbQuery {
        LeadResultRow.getHostResults(hostId, limit)
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
        url = row[LeadTable.url].toCheckedFromDb(),
        targetId = row[LeadTable.sourceId]?.value,
        hostId = row[LeadTable.hostId].value,
        feedHeadline = row.getOrNull(LeadJobTable.headline),
        lastAttemptAt = row.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO),
        isExternal = row[LeadJobTable.isExternal],
        freshAt = row.getOrNull(LeadJobTable.freshAt)?.toInstant(UtcOffset.ZERO),
        linkCount = row[LinkTable.leadId.count()].toInt()
    )
}