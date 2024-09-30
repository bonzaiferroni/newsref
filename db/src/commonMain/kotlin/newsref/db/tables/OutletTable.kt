package newsref.db.tables

import newsref.db.tables.OutletTable.domains
import newsref.db.tables.SourceContentTable.contentId
import newsref.db.tables.SourceContentTable.sourceId
import newsref.model.data.Outlet
import newsref.model.dto.SourceInfo
import newsref.model.utils.getApexDomain
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.anyFrom
import org.jetbrains.exposed.sql.stringParam

object OutletTable : IntIdTable("outlet") {
    val name = text("name").nullable()
    val logo = text("logo").nullable()
    val robotsTxt = text("robots_txt").nullable()
    val disallowed = array<String>("disallowed").nullable()
    val domains = array<String>("domains")
    val urlParams = array<String>("url_params")
}

class OutletRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, OutletRow>(OutletTable)

    var authors by AuthorRow via OutletAuthorTable

    var name by OutletTable.name
    var logo by OutletTable.logo
    var robotsTxt by OutletTable.robotsTxt
    var disallowed by OutletTable.disallowed
    var domains by OutletTable.domains
    var urlParams by OutletTable.urlParams

    val sources by SourceRow referrersOn SourceTable.outletId
}

fun OutletRow.toData() = Outlet(
    id = this.id.value,
    name = this.name,
    logo = this.logo,
    robotsTxt = this.robotsTxt,
    disallowed = this.disallowed?.toSet(),
    domains = this.domains.toSet(),
    urlParams = this.urlParams.toSet(),
)

fun OutletRow.fromData(outlet: Outlet) {
    name = outlet.name
    logo = outlet.logo
    robotsTxt = outlet.robotsTxt
    disallowed = outlet.disallowed?.toList()
    domains = outlet.domains.toList()
    urlParams = outlet.urlParams.toList()
}

fun SourceInfo.toOutlet(): Outlet = Outlet(
    name = outletName,
    domains = setOf(source.url.getApexDomain().lowercase()),
    urlParams = emptySet(),
)

fun OutletRow.Companion.findByApex(apex: String): OutletRow? =
    OutletRow.find { stringParam(apex) eq anyFrom(OutletTable.domains) }.firstOrNull()
