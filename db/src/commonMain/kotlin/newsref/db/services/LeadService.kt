package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.model.core.CheckedUrl
import newsref.model.data.Host
import newsref.model.data.ResultType
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

    suspend fun addSource(leadId: Long, sourceId: Long) = dbQuery {
        val leadRow = LeadRow[leadId]
        leadRow.target = SourceRow[sourceId]
    }

    suspend fun createIfFreshLead(url: CheckedUrl, host: Host) = dbQuery {
        if (LeadRow.leadExists(url))
            throw IllegalArgumentException("Lead already exists: $url")

        val hostRow = HostRow.findById(host.id)
            ?: throw IllegalArgumentException("Host missing: ${url.domain}")

        val leadRow = LeadRow.new { this.url = url.toString(); this.host = hostRow }
        leadRow.toData() // return
    }

    suspend fun addResult(leadId: Long, outletId: Int, result: ResultType) = dbQuery {
//        LeadResultRow.new {
//            lead = LeadRow[leadId]
//            outlet = OutletRow[outletId]
//            this.result = result
//            attemptedAt = Clock.nowToLocalDateTimeUTC()
//        }.toData()
    }

    suspend fun leadExists(checkedUrl: CheckedUrl) = dbQuery { LeadRow.leadExists(checkedUrl) }

    suspend fun getResultsByOutlet(outletId: Int, since: Duration) = dbQuery {
        LeadRow.getOutletResults(outletId, since)
    }
}