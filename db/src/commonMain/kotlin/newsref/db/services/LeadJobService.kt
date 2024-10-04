package newsref.db.services

import jdk.internal.org.jline.utils.Colors.s
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.LeadTable
import newsref.model.data.Lead
import newsref.model.data.LeadJob
import org.jetbrains.exposed.sql.lowerCase

class LeadJobService : DbService() {

	suspend fun createIfFreshLead(leadJob: LeadJob) = dbQuery {
		val urlString = leadJob.url.toString()
		val leadRow = LeadRow.find { LeadTable.url.lowerCase() eq urlString.lowercase() }.firstOrNull()
		if (leadRow != null)
			throw IllegalArgumentException("Lead already exists: ${leadJob.url}")
		val outletRow = OutletRow.findByHost(leadJob.url.host)
			?: throw IllegalArgumentException("Outlet missing: ${leadJob.url.host}")

		if (outletRow.disallowed.any { leadJob.url.path.startsWith(it) })
			throw IllegalArgumentException("Lead path is disallowed")

		LeadRow.new { url = leadJob.url.toString() }
		val feedRow = FeedRow.find { FeedTable.id eq leadJob.feedId }.firstOrNull()
			?: throw IllegalArgumentException("Feed does not exist: ${leadJob.feedId}")
		val leadJobRow = LeadJobRow.new { fromData(leadJob, feedRow) }
		leadJobRow.toData() // return
	}

	suspend fun addAttempt(leadJob: LeadJob) = dbQuery {
		val leadRow = LeadJobRow[leadJob.id]
		leadRow.attemptCount++
		leadRow.attemptedAt = leadJob.attemptedAt?.toLocalDateTime(TimeZone.UTC)
	}
}