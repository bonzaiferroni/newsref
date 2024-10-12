package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.nowToLocalDateTimeUTC
import newsref.db.utils.toCheckedFromDb
import newsref.model.core.CheckedUrl
import newsref.model.data.Lead
import newsref.model.data.LeadInfo
import newsref.model.data.Outlet
import newsref.model.data.ResultType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.count
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class LeadService : DbService() {

    suspend fun getOpenJobs() = dbQuery {
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

    suspend fun createIfFreshLead(url: CheckedUrl, outlet: Outlet) = dbQuery {
        if (LeadRow.leadExists(url))
            throw IllegalArgumentException("Lead already exists: $url")

        val outletRow = OutletRow.findById(outlet.id)
            ?: throw IllegalArgumentException("Outlet missing: ${url.host}")

        val leadRow = LeadRow.new { this.url = url.toString(); this.outlet = outletRow }
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