package newsref.db.services

import com.sun.jndi.toolkit.url.Uri
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import newsref.db.DataService
import newsref.db.tables.*
import newsref.db.tables.OutletTable.disallowed
import newsref.model.data.Feed
import newsref.model.data.Lead
import newsref.model.utils.getApexDomain
import newsref.model.utils.removeQueryParameters
import org.jetbrains.exposed.sql.lowerCase

class LeadService : DataService<Lead, Long, LeadRow>(
    LeadRow,
    { it.id },
    LeadRow::fromData,
    LeadRow::toData
) {
    suspend fun createIfFresh(url: String, feedId: Int? = null, headline: String? = null): Lead? = dbQuery {
        val leadRow = LeadRow.find { LeadTable.url.lowerCase() eq url.lowercase() }.firstOrNull()
        if (leadRow != null) return@dbQuery null
        LeadRow.new {
            this.url = url
            this.attemptCount = 0
            this.headline = headline
            this.feed = feedId?.let { FeedRow[it] }
        }.toData() // return
    }

    suspend fun getUnfollowed(): List<Lead> = dbQuery {
        LeadRow.find { LeadTable.targetId.isNull() }
            .map { it.toData() }
    }

    suspend fun addAttempt(lead: Lead) = dbQuery {
        val leadRow = LeadRow[lead.id]
        leadRow.attemptCount++
        leadRow.attemptedAt = lead.attemptedAt?.toLocalDateTime(TimeZone.UTC)
    }

    suspend fun addSource(lead: Lead, sourceId: Long) = dbQuery {
        val leadRow = LeadRow[lead.id]
        leadRow.target = SourceRow[sourceId]
    }
}