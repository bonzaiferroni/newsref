package newsref.db.tables

import newsref.db.utils.sameAs
import newsref.model.data.Host
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

internal object HostTable : IntIdTable("host") {
	val nexusId = reference("nexus_id", NexusTable, ReferenceOption.SET_NULL).nullable()

	val core = text("core").uniqueIndex()
	val name = text("name").nullable()
	val logo = text("logo").nullable()
	val robotsTxt = text("robots_txt").nullable()
	val isRedirect = bool("is_redirect").nullable()
	val disallowed = array<String>("disallowed")
	val domains = array<String>("domains")
	val junkParams = array<String>("junk_params")
	val navParams = array<String>("nav_params")
}

internal class HostRow(id: EntityID<Int>) : IntEntity(id) {
	companion object : EntityClass<Int, HostRow>(HostTable)

	var nexus by NexusRow optionalReferencedOn HostTable.nexusId

	var core by HostTable.core
	var name by HostTable.name
	var logo by HostTable.logo
	var robotsTxt by HostTable.robotsTxt
	var isRedirect by HostTable.isRedirect
	var bannedPaths by HostTable.disallowed
	var domains by HostTable.domains
	var junkParams by HostTable.junkParams
	var navParams by HostTable.navParams

	var authors by AuthorRow via HostAuthorTable
	val sources by SourceRow referrersOn SourceTable.hostId
	val leads by LeadRow referrersOn LeadTable.hostId
}

internal fun HostRow.toData() = Host(
	id = this.id.value,
	nexusId = this.nexus?.id?.value,
	core = this.core,
	name = this.name,
	logo = this.logo,
	robotsTxt = this.robotsTxt,
	isRedirect = this.isRedirect,
	bannedPaths = this.bannedPaths.toSet(),
	domains = this.domains.toSet(),
	junkParams = this.junkParams.toSet(),
	navParams = this.navParams.toSet(),
)

internal fun HostRow.fromData(host: Host, nexusRow: NexusRow? = null) {
	nexusRow?.let { nexus = nexusRow }
	core = host.core
	name = host.name
	logo = host.logo
	robotsTxt = host.robotsTxt
	isRedirect = host.isRedirect
	bannedPaths = host.bannedPaths.toList()
	domains = host.domains.toList()
	junkParams = host.junkParams.toList()
	navParams = host.navParams.toList()
}

internal fun HostRow.Companion.findByCore(core: String): HostRow? {
	return this.find { HostTable.core.sameAs(core) }.firstOrNull()
}