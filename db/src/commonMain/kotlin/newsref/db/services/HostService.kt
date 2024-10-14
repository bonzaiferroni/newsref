package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.HostRow
import newsref.db.tables.findByHost
import newsref.db.tables.toData
import newsref.model.core.Url
import newsref.model.data.Host

class HostService : DbService(){
    suspend fun findByHost(host: String): Host? = dbQuery {
        HostRow.findByHost(host.removePrefix("www."))
    }?.toData()

    suspend fun createHost(
        url: Url,
        robotsTxt: String?,
        isRedirect: Boolean,
        bannedPaths: Set<String>,
    ) = dbQuery {
        val domain = url.domain
        val domains = mutableListOf(domain)
        if (domain.startsWith("www.")) domains.add(domain.removePrefix("www."))
        HostRow.new {
            this.apex = url.apex
            this.robotsTxt = robotsTxt
            this.isRedirect = isRedirect
            this.domains = domains
            this.bannedPaths = bannedPaths.toList()
        }.toData()
    }
}