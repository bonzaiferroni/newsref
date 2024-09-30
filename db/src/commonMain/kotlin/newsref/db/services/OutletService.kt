package newsref.db.services

import newsref.db.DataService
import newsref.db.tables.OutletRow
import newsref.db.tables.findByApex
import newsref.db.tables.fromData
import newsref.db.tables.toData
import newsref.model.data.Outlet

class OutletService : DataService<Outlet, Int, OutletRow>(
    OutletRow,
    { it.id },
    OutletRow::fromData,
    OutletRow::toData
) {
    suspend fun findByApex(apex: String): Outlet? = dbQuery { OutletRow.findByApex(apex) }?.toData()
    suspend fun createOutlet(
        apex: String,
        robotsTxt: String?,
        disallowed: Set<String>?
    ) = dbQuery {
        OutletRow.new {
            this.domains = listOf(apex)
            this.robotsTxt = robotsTxt
            this.disallowed = disallowed?.toList()
            this.urlParams = emptyList()
        }.toData()
    }
}