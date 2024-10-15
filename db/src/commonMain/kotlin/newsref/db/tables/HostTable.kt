package newsref.db.tables

import newsref.model.data.Host
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.anyFrom
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.stringParam

internal object HostTable : IntIdTable("host") {
	val core = text("apex")
	val name = text("name").nullable()
	val logo = text("logo").nullable()
	val robotsTxt = text("robots_txt").nullable()
	val isRedirect = bool("is_redirect").nullable()
	val disallowed = array<String>("disallowed")
	val domains = array<String>("domains")
	val junkParams = array<String>("junk_params")
}

internal class HostRow(id: EntityID<Int>) : IntEntity(id) {
	companion object : EntityClass<Int, HostRow>(HostTable)

	var authors by AuthorRow via HostAuthorTable

	var core by HostTable.core
	var name by HostTable.name
	var logo by HostTable.logo
	var robotsTxt by HostTable.robotsTxt
	var isRedirect by HostTable.isRedirect
	var bannedPaths by HostTable.disallowed
	var domains by HostTable.domains
	var junkParams by HostTable.junkParams

	val sources by SourceRow referrersOn SourceTable.hostId
	val leads by LeadRow referrersOn LeadTable.hostId
}

internal fun HostRow.toData() = Host(
	id = this.id.value,
	core = this.core,
	name = this.name,
	logo = this.logo,
	robotsTxt = this.robotsTxt,
	isRedirect = this.isRedirect,
	bannedPaths = this.bannedPaths.toSet(),
	domains = this.domains.toSet(),
	junkParams = this.junkParams.toSet(),
)

internal fun HostRow.fromData(host: Host) {
	core = host.core
	name = host.name
	logo = host.logo
	robotsTxt = host.robotsTxt
	isRedirect = host.isRedirect
	bannedPaths = host.bannedPaths.toList()
	domains = host.domains.toList()
	junkParams = host.junkParams.toList()
}

internal fun HostRow.Companion.findByDomain(host: String): HostRow? {
	// Prepare host variants outside the query block
	val queries = setOf(host, host.removePrefix("www."), "www.$host").map {
		stringParam(it) eq anyFrom(HostTable.domains)
	}
	return this.find { queries.reduce { acc, query -> acc or query } }.firstOrNull()
}
