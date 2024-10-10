package newsref.db.services

import newsref.db.DataService
import newsref.db.DbService
import newsref.db.tables.OutletRow
import newsref.db.tables.findByHost
import newsref.db.tables.newFromData
import newsref.db.tables.toData
import newsref.model.data.Outlet

class OutletService : DbService(){
    suspend fun findByHost(host: String): Outlet? = dbQuery {
        OutletRow.findByHost(host.removePrefix("www."))
    }?.toData()

    suspend fun createOutlet(
        host: String,
        robotsTxt: String?,
        disallowed: Set<String>,
        keepParams: Set<String>
    ) = dbQuery {
        val hosts = mutableListOf(host)
        if (host.startsWith("www.")) hosts.add(host.removePrefix("www."))
        OutletRow.new {
            this.domains = hosts
            this.robotsTxt = robotsTxt
            this.disallowed = disallowed.toList()
            this.urlParams = keepParams.toList()
        }.toData()
    }
}