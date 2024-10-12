package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DataService
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.OutletRow
import newsref.db.tables.findByHost
import newsref.db.tables.toData
import newsref.db.utils.toLocalDateTimeUTC
import newsref.model.data.Outlet
import org.jetbrains.exposed.sql.and
import kotlin.time.Duration

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