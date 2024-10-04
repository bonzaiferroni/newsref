package newsref.db.services

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import newsref.db.DataService
import newsref.db.tables.*
import newsref.model.core.Url
import newsref.model.data.Lead
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.lowerCase

class LeadService : DataService<Lead, Long, LeadRow>(
    LeadRow,
    { it.id },
    LeadRow::fromData,
    LeadRow::toData
) {
    suspend fun createIfFresh(url: Url, feedId: Int? = null, headline: String? = null): Lead? = dbQuery {
        val urlString = url.toString()
        val leadRow = LeadRow.find { LeadTable.url.lowerCase() eq urlString.lowercase() }.firstOrNull()
        if (leadRow != null) return@dbQuery null
        LeadRow.new {
            this.url = urlString
        }.toData() // return
    }

    suspend fun getUnfollowed(): List<Lead> = dbQuery {
        LeadRow.find { LeadTable.targetId.isNull() }.map { it.toData() }
    }

    suspend fun addSource(lead: Lead, sourceId: Long) = dbQuery {
        val leadRow = LeadRow[lead.id]
        leadRow.target = SourceRow[sourceId]
    }
}