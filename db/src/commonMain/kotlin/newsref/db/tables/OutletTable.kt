package newsref.db.tables

import newsref.model.data.Outlet
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.anyFrom
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.stringParam

internal object OutletTable : IntIdTable("outlet") {
	val name = text("name").nullable()
	val logo = text("logo").nullable()
	val robotsTxt = text("robots_txt").nullable()
	val disallowed = array<String>("disallowed")
	val domains = array<String>("domains")
	val urlParams = array<String>("url_params")
}

internal class OutletRow(id: EntityID<Int>) : IntEntity(id) {
	companion object : EntityClass<Int, OutletRow>(OutletTable)

	var authors by AuthorRow via OutletAuthorTable

	var name by OutletTable.name
	var logo by OutletTable.logo
	var robotsTxt by OutletTable.robotsTxt
	var disallowed by OutletTable.disallowed
	var domains by OutletTable.domains
	var urlParams by OutletTable.urlParams

	val sources by SourceRow referrersOn SourceTable.outletId
	val leadResults by LeadResultRow referrersOn LeadResultTable.outletId
}

internal fun OutletRow.toData() = Outlet(
	id = this.id.value,
	name = this.name,
	logo = this.logo,
	robotsTxt = this.robotsTxt,
	disallowed = this.disallowed.toSet(),
	domains = this.domains.toSet(),
	junkParams = this.urlParams.toSet(),
)

internal fun OutletRow.newFromData(outlet: Outlet) {
	name = outlet.name
	logo = outlet.logo
	robotsTxt = outlet.robotsTxt
	disallowed = outlet.disallowed.toList()
	domains = outlet.domains.toList()
	urlParams = outlet.junkParams.toList()
}

internal fun OutletRow.Companion.findByHost(host: String): OutletRow? {
	// Prepare host variants outside the query block
	val queries = setOf(host, host.removePrefix("www."), "www.$host").map {
		stringParam(it) eq anyFrom(OutletTable.domains)
	}
	return this.find { queries.reduce { acc, query -> acc or query } }.firstOrNull()
}
