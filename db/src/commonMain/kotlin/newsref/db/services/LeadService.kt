package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.nowToLocalDateTimeUTC
import newsref.model.core.CheckedUrl
import newsref.model.data.Lead
import newsref.model.data.LeadJob
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull

class LeadService : DbService() {

    suspend fun getOpenJobs() = dbQuery {
        val query = LeadTable.leftJoin(LeadJobTable)
            .select(LeadJobTable.columns)
            .where(LeadTable.targetId.isNull())
        LeadJobRow.wrapRows(query).toList().map { it.toData() }
    }

    suspend fun addSource(leadId: Long, sourceId: Long) = dbQuery {
        val leadRow = LeadRow[leadId]
        leadRow.target = SourceRow[sourceId]
    }

    suspend fun createIfFreshLead(leadJob: LeadJob) = dbQuery {
        if (LeadRow.leadExists(leadJob.url))
            throw IllegalArgumentException("Lead already exists: ${leadJob.url}")

        val outletRow = OutletRow.findByHost(leadJob.url.host)
            ?: throw IllegalArgumentException("Outlet missing: ${leadJob.url.host}")

        if (outletRow.disallowed.any { leadJob.url.path.startsWith(it) })
            throw IllegalArgumentException("Lead path is disallowed")

        val leadRow = LeadRow.new { url = leadJob.url.toString() }
        val feedRow = FeedRow.find { FeedTable.id eq leadJob.feedId }.firstOrNull()
        val leadJobRow = LeadJobRow.new { fromData(leadJob, leadRow, feedRow) }
        leadJobRow.toData() // return
    }

    suspend fun addAttempt(leadJob: LeadJob) = dbQuery {
        val leadRow = LeadJobRow[leadJob.id]
        leadRow.attemptCount++
        leadRow.attemptedAt = Clock.nowToLocalDateTimeUTC()
    }

    suspend fun leadExists(checkedUrl: CheckedUrl) = dbQuery { LeadRow.leadExists(checkedUrl) }

    suspend fun getUnfollowed(): List<Lead> = dbQuery {
        LeadRow.find { LeadTable.targetId.isNull() }.map { it.toData() }
    }
}