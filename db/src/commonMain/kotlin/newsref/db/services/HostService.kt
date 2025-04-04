package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.*
import newsref.db.core.Url
import newsref.db.model.Host
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.data.ContentType
import newsref.model.data.DataSort
import newsref.model.data.SortDirection
import org.jetbrains.exposed.sql.*
import kotlin.time.Duration

class HostService : DbService() {
    suspend fun findByUrl(url: Url): Host? = dbQuery {
        HostTable.readFirstOrNull { HostTable.core.sameAs(url.core) }?.toHost()
    }

    suspend fun findById(hostId: Int) = dbQuery {
        HostTable.readById(hostId).toHost()
    }

    suspend fun createHost(
        url: Url,
        robotsTxt: String?,
        isRedirect: Boolean,
        bannedPaths: Set<String>,
    ) = dbQuery {
        val domain = url.domain
        val domains = mutableListOf(domain)
        if (domain.startsWith("www.")) domains.add(domain.removePrefix("www."))

        val hostId = HostTable.insertAndGetId {
            it[this.core] = url.core
            it[this.robotsTxt] = robotsTxt
            it[this.isRedirect] = isRedirect
            it[this.score] = 0
            it[this.junkParams] = emptyList()
            it[this.navParams] = emptyList()
            it[this.bannedPaths] = bannedPaths.toList()
            it[this.domains] = domains.toList()
        }.value

        HostTable.readById(hostId).toHost()
    }

    suspend fun updateParameters(hostId: Int, junkParams: Set<String>?, navParams: Set<String>?) = dbQuery {
        val host = HostTable.readById(hostId).toHost()
        if (junkParams == null && navParams == null) return@dbQuery host

        HostTable.updateById(hostId) {
            if (junkParams != null) it[this.junkParams] = host.junkParams.smoosh(junkParams)
            if (navParams != null) it[this.navParams] = host.navParams.smoosh(navParams)
        }
        HostTable.readById(hostId).toHost()
    }

    suspend fun readHosts(searchText: String? = null, limit: Int = 100) = dbQuery {
        val query = when {
            !searchText.isNullOrEmpty() -> HostTable.selectAll().where { HostTable.core like "$searchText%" }
            else -> HostTable.selectAll()
        }
        query.orderBy(HostTable.score, SortOrder.DESC)
            .limit(limit)
            .map { it.toHost() }
    }

    suspend fun readHost(hostId: Int) = dbQuery {
        HostTable.selectAll()
            .where { HostTable.id eq hostId }
            .firstOrNull()?.toHost()
    }

    suspend fun readHostSources(
        hostId: Int,
        interval: Duration,
        searchText: String,
        sort: DataSort?,
        direction: SortDirection?,
        limit: Int = 100
    ) = dbQuery {
        val time = Clock.System.now() - interval
        val orderColumn = when (sort ?: DataSort.Id) {
            DataSort.Id -> PageTable.id
            DataSort.Time -> PageTable.seenAt
            DataSort.Name -> PageTable.title
            DataSort.Score -> PageTable.score
        }
        sourceInfoTables
            .where {
                PageTable.hostId.eq(hostId) and PageTable.title.like("%$searchText%") and
                        (PageTable.contentType.eq(ContentType.NewsArticle) or PageTable.contentType.eq(ContentType.SocialPost)) and
                        PageTable.existedAfter(time)
            }
            .orderBy(orderColumn, (direction ?: SortDirection.Descending).toSortOrder())
            .limit(limit)
            .map { it.toPageInfo() }
    }
}

private fun <T> Set<T>.smoosh(list: Collection<T>) = (this + list).toSet().toList()