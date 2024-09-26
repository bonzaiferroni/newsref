package newsref.db.tables

import newsref.model.data.Outlet
import newsref.model.data.Source
import newsref.model.dto.ArticleInfo
import newsref.model.utils.getApexDomain
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object OutletTable : IntIdTable("outlet") {
    val name = text("name").nullable()
    val apex = text("apex")
    val domains = array<String>("domains")
}

class OutletEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, OutletEntity>(OutletTable)

    var name by OutletTable.name
    var apex by OutletTable.apex
    var domains by OutletTable.domains

    val sources by SourceEntity referrersOn SourceTable.outletId
}

fun OutletEntity.toData() = Outlet(
    id = this.id.value,
    name = this.name,
    apex = this.apex,
    domains = this.domains.toSet()
)

fun OutletEntity.fromData(outlet: Outlet) {
    name = outlet.name
    apex = outlet.apex
    domains = outlet.domains.toList()
}

fun ArticleInfo.toOutlet(): Outlet = Outlet(
    name = outletName,
    apex = source.url.getApexDomain(),
    domains = setOf(source.url.getApexDomain())
)