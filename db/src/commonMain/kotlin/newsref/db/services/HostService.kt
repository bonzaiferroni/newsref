package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.*
import newsref.db.core.Url
import newsref.db.model.Host
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.time.Duration

class HostService : DbService() {
    suspend fun findByUrl(url: Url): Host? = dbQuery {
        HostRow.find { HostTable.core.sameAs(url.core) }.firstOrNull()?.toModel()
    }

    suspend fun findById(hostId: Int) = dbQuery {
        HostRow.find(HostTable.id eq hostId).firstOrNull()?.toModel()
            ?: throw IllegalArgumentException("Host $hostId not found")
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
        val host = Host(
            core = url.core,
            robotsTxt = robotsTxt,
            isRedirect = isRedirect,
            score = 0,
            domains = domains.toSet(),
            bannedPaths = bannedPaths.toSet(),
        )
        HostRow.new { fromModel(host) }.toModel()
    }

    suspend fun updateParameters(host: Host, junkParams: Set<String>?, navParams: Set<String>?) = dbQuery {
        val hostRow = HostRow.find(HostTable.id eq host.id).firstOrNull()
            ?: throw IllegalArgumentException("Host ${host.core} not found")
        junkParams?.let { hostRow.junkParams = it.smoosh(hostRow.junkParams) }
        navParams?.let { hostRow.navParams = it.smoosh(hostRow.navParams) }
        hostRow.toModel()
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
            DataSort.Id -> SourceTable.id
            DataSort.Time -> SourceTable.seenAt
            DataSort.Name -> SourceTable.title
            DataSort.Score -> SourceTable.score
        }
        sourceInfoTables
            .where {
                SourceTable.hostId.eq(hostId) and SourceTable.title.like("%$searchText%") and
                        (SourceTable.type.eq(PageType.NEWS_ARTICLE) or SourceTable.type.eq(PageType.SOCIAL_POST)) and
                        SourceTable.existedAfter(time)
            }
            .orderBy(orderColumn, (direction ?: SortDirection.Descending).toSortOrder())
            .limit(limit)
            .map { it.toSourceInfo() }
    }
}

private fun <T> Set<T>.smoosh(list: List<T>) = this.toMutableSet().also { set -> set.addAll(list) }.toList()