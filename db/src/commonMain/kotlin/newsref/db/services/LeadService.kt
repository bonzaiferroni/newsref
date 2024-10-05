package newsref.db.services

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import newsref.db.DbService
import newsref.db.tables.*
import newsref.model.core.CheckedUrl
import newsref.model.data.Lead
import newsref.model.data.LeadJob

class LeadService : DbService() {

    suspend fun getJobs() = dbQuery { LeadJobRow.all().map { it.toData() } }

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
        leadRow.attemptedAt = leadJob.attemptedAt?.toLocalDateTime(TimeZone.UTC)
    }

    suspend fun leadExists(checkedUrl: CheckedUrl) = dbQuery { LeadRow.leadExists(checkedUrl) }

    suspend fun getUnfollowed(): List<Lead> = dbQuery {
        LeadRow.find { LeadTable.targetId.isNull() }.map { it.toData() }
    }
}