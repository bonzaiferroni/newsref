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
    suspend fun findByHost(url: Url): Outlet? = dbQuery { OutletRow.findByHost(url) }?.toData()

    suspend fun findAndSetName(url: Url, name: String?): Outlet? = dbQuery {
        val row = OutletRow.findByHost(url) ?: return@dbQuery null
        name?.let { row.name = name }
        row.toData()
    }

    suspend fun createOutlet(
        url: Url,
        robotsTxt: String?,
        disallowed: Set<String>?,
        name: String?
    ) = dbQuery {
        OutletRow.new {
            this.domains = listOf(url.host)
            this.robotsTxt = robotsTxt
            this.disallowed = disallowed?.toList()
            this.urlParams = emptyList()
            this.name = name
        }.toData()
    }
}