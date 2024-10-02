package newsref.db.services

import com.eygraber.uri.Url
import newsref.db.DataService
import newsref.db.tables.OutletRow
import newsref.db.tables.findByHost
import newsref.db.tables.fromData
import newsref.db.tables.toData
import newsref.model.data.Outlet

class OutletService : DataService<Outlet, Int, OutletRow>(
    OutletRow,
    { it.id },
    OutletRow::fromData,
    OutletRow::toData
) {
    suspend fun findByHost(host: String): Outlet? = dbQuery { OutletRow.findByHost(host) }?.toData()

    suspend fun findAndSetName(url: Url, name: String?): Outlet = dbQuery {
        val row = OutletRow.findByHost(url.host)
            ?: throw IllegalArgumentException("Outlet not found: ${url.host}")
        name?.let { row.name = name }
        row.toData()
    }

    suspend fun createOutlet(
        host: String,
        robotsTxt: String?,
        disallowed: Set<String>?,
        keepParams: Set<String>
    ) = dbQuery {
        OutletRow.new {
            this.domains = listOf(host)
            this.robotsTxt = robotsTxt
            this.disallowed = disallowed?.toList()
            this.urlParams = keepParams.toList()
        }.toData()
    }
}