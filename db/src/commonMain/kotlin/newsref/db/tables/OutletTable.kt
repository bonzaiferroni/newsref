package newsref.db.tables

import newsref.model.data.Outlet
import newsref.model.dto.SourceInfo
import newsref.model.utils.getApexDomain
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object OutletTable : IntIdTable("outlet") {
    val name = text("name").nullable()
    val domains = array<String>("domains")
    val urlParams = array<String>("url_params")
}

class OutletRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, OutletRow>(OutletTable)

    var name by OutletTable.name
    var domains by OutletTable.domains
    var urlParams by OutletTable.urlParams

    val sources by SourceRow referrersOn SourceTable.outletId
}

fun OutletRow.toData() = Outlet(
    id = this.id.value,
    name = this.name,
    domains = this.domains.toSet(),
    urlParams = this.urlParams.toSet(),
)

fun OutletRow.fromData(outlet: Outlet) {
    name = outlet.name
    domains = outlet.domains.toList()
    urlParams = outlet.urlParams.toList()
}

fun SourceInfo.toOutlet(): Outlet = Outlet(
    name = outletName,
    domains = setOf(source.url.getApexDomain().lowercase()),
    urlParams = emptySet(),
)