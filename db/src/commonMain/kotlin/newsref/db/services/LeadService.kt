package newsref.db.services

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.db.utils.sameUrl
import newsref.db.utils.toCheckedFromTrusted
import newsref.db.utils.toSqlString
import newsref.model.core.CheckedUrl
import newsref.model.core.Url
import newsref.model.data.LeadJob
import newsref.model.data.LeadInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull

private val console = globalConsole.getHandle("LeadService")

class LeadService : DbService() {

    suspend fun getOpenLeads(limit: Int = 20000) = dbQuery {
        leadInfoJoin.where(LeadTable.sourceId.isNull())
            .orderBy(linkCountAlias, SortOrder.DESC)
            .orderBy(LeadJobTable.isExternal, SortOrder.DESC)
            .orderBy(LeadJobTable.freshAt, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            // .toSqlString { console.log(it) }
            .toLeadInfos()
    }

    suspend fun createOrLinkLead(url: CheckedUrl, leadJob: LeadJob?, createIfFresh: Boolean) = dbQuery {
        var leadRow = LeadRow.find(LeadTable.url.sameUrl(url)).firstOrNull()
        if (leadRow != null) {
            // assign lead to links that might already exist from other sources
            val affirmed = LinkRow.setLeadOnSameLinks(url, leadRow)
            return@dbQuery if (affirmed) CreateLeadResult.AFFIRMED else CreateLeadResult.IRRELEVANT
        }
        if (!createIfFresh) return@dbQuery CreateLeadResult.IRRELEVANT

        leadRow = LeadRow.createOrUpdateAndLink(url)

        leadJob?.let { job ->
            val feedRow = job.feedId?.let { FeedRow[it] }
            LeadJobRow.new { fromData(leadJob, leadRow, feedRow) }
        }
        CreateLeadResult.CREATED // return
    }

    suspend fun getResultsByHost(hostId: Int, limit: Int) = dbQuery {
        LeadResultRow.getHostResults(hostId, limit)
    }

    suspend fun getLeadsFromFeed(feedId: Int) = dbQuery {
        leadInfoJoin.where { LeadJobTable.feedId eq feedId }
            .orderBy(LeadJobTable.id, SortOrder.DESC)
            .limit(100)
            .toLeadInfos()
    }

    suspend fun getAllFeedLeads() = dbQuery {
        LeadJobTable.selectAll().where { LeadJobTable.feedId.isNotNull() }.map { it.toLeadJob() }
    }

    suspend fun readLeadInfoByUrl(url: Url) = dbQuery {
        leadInfoJoin.where { LeadTable.url.sameUrl(url) }
            .toLeadInfos().firstOrNull()
    }
}

class LeadExistsException(url: CheckedUrl) : IllegalArgumentException("Lead already exists: $url")

// lead info

enum class CreateLeadResult {
    CREATED,
    AFFIRMED,
    ERROR,
    IRRELEVANT
}