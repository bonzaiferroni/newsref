package newsref.db.services

import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.db.utils.sameUrl
import newsref.db.core.CheckedUrl
import newsref.db.core.Url
import newsref.db.model.LeadJob
import newsref.db.utils.readFirstOrNull
import newsref.db.utils.toLocalDateTimeUtc
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull

private val console = globalConsole.getHandle("LeadService")

class LeadService : DbService() {

    suspend fun getOpenLeads(limit: Int = 20000) = dbQuery {
        leadInfoJoin.where(LeadTable.pageId.isNull())
            .orderBy(
                Pair(linkCountAlias, SortOrder.DESC),
                Pair(LeadJobTable.isExternal, SortOrder.DESC_NULLS_LAST),
                Pair(LeadJobTable.feedPosition, SortOrder.ASC_NULLS_LAST),
                Pair(LeadJobTable.freshAt, SortOrder.DESC_NULLS_LAST)
            )
            .limit(limit)
            // .toSqlString { console.log(it) }
            .toLeadInfos()
    }

    suspend fun createOrLinkLead(url: CheckedUrl, leadJob: LeadJob?, createIfFresh: Boolean) = dbQuery {
        val lead = LeadTable.readFirstOrNull { it.url.sameUrl(url) }?.toLead()
        if (lead != null) {
            // assign lead to links that might already exist from other sources
            val affirmed = LinkTable.setLeadOnSameLinks(url, lead.id)
            return@dbQuery if (affirmed) CreateLeadResult.AFFIRMED else CreateLeadResult.IRRELEVANT
        }
        if (!createIfFresh) return@dbQuery CreateLeadResult.IRRELEVANT

        val leadId = LeadTable.createOrUpdateAndLink(url)

        leadJob?.let { job ->
            LeadJobTable.insert {
                it[this.leadId] = leadId
                it[this.feedId] = job.feedId
                it[this.headline] = job.headline
                it[this.isExternal] = job.isExternal
                it[this.freshAt] = job.freshAt?.toLocalDateTimeUtc()
                it[this.feedPosition] = job.feedPosition
            }
        }
        CreateLeadResult.CREATED // return
    }

    suspend fun getResultsByHost(hostId: Int, limit: Int) = dbQuery {
        LeadResultTable.getHostResults(hostId, limit)
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

// lead info

enum class CreateLeadResult {
    CREATED,
    AFFIRMED,
    ERROR,
    IRRELEVANT
}