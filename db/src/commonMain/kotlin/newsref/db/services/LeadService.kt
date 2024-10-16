package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.model.core.CheckedUrl
import newsref.model.data.LeadJob
import newsref.model.data.Host
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

    suspend fun getResultsByOutlet(outletId: Int, since: Duration) = dbQuery {
        LeadRow.getOutletResults(outletId, since)
    }
}

class LeadExistsException(url: CheckedUrl) : IllegalArgumentException("Lead already exists: $url")