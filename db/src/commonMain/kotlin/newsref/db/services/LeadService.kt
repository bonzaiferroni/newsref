package newsref.db.services

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.toCheckedFromDb
import newsref.model.core.CheckedUrl
import newsref.model.data.LeadJob
import newsref.model.data.Host
import newsref.model.data.LeadInfo
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.count
import kotlin.time.Duration

class LeadService : DbService() {

    suspend fun getOpenLeads() = dbQuery {
        LeadTable.leftJoin(LeadJobTable).leftJoin(LeadResultTable)
            .select(leadInfoColumns + LeadResultTable.leadId.count())
            .where(LeadTable.targetId.isNull())
			.groupBy(*leadInfoColumns.toTypedArray())
			.wrapLeadInfo()
    }

    suspend fun createIfFreshLead(url: CheckedUrl, host: Host, leadJob: LeadJob?) = dbQuery {
        if (LeadRow.leadExists(url))
            throw LeadExistsException(url)

        val hostRow = HostRow.findById(host.id)
            ?: throw IllegalArgumentException("Host missing: ${url.domain}")

        val leadRow = LeadRow.new { this.url = url.toString(); this.host = hostRow }

        leadJob?.let { job ->
            val feedRow = job.feedId?.let { FeedRow[it] }
            LeadJobRow.new { fromData(leadJob, leadRow, feedRow) }
        }
        leadRow.toData() // return
    }

    suspend fun getResultsByHost(hostId: Int, since: Duration) = dbQuery {
        LeadRow.getHostResults(hostId, since)
    }
}

class LeadExistsException(url: CheckedUrl) : IllegalArgumentException("Lead already exists: $url")

// lead info
internal val leadInfoColumns = listOf(
    LeadTable.id,
    LeadTable.url,
    LeadTable.targetId,
    LeadTable.hostId,
    LeadJobTable.headline,
    LeadJobTable.isExternal,
    LeadJobTable.freshAt,
    // LeadResultTable.leadId.count()
)

internal fun Query.wrapLeadInfo() = this.map { row ->
    LeadInfo(
        id = row[LeadTable.id].value,
        url = row[LeadTable.url].toCheckedFromDb(),
        targetId = row[LeadTable.targetId]?.value,
        hostId = row[LeadTable.hostId].value,
        feedHeadline = row.getOrNull(LeadJobTable.headline),
        attemptCount = row[LeadResultTable.leadId.count()].toInt(),
        lastAttemptAt = row.getOrNull(LeadResultTable.attemptedAt)?.toInstant(UtcOffset.ZERO),
        isExternal = row[LeadJobTable.isExternal],
        freshAt = row.getOrNull(LeadJobTable.freshAt)?.toInstant(UtcOffset.ZERO)
    )
}