package newsref.db.tables

import newsref.db.utils.sameAs
import newsref.db.model.Host
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

internal object HostTable : IntIdTable("host") {
	val nexusId = reference("nexus_id", NexusTable, ReferenceOption.SET_NULL).nullable().index()

	val core = text("core").uniqueIndex()
	val name = text("name").nullable()
	val logo = text("logo").nullable()
	val robotsTxt = text("robots_txt").nullable()
	val isRedirect = bool("is_redirect").nullable()
	val score = integer("score").default(0)
	val bannedPaths = array<String>("banned_paths")
	val domains = array<String>("domains")
	val junkParams = array<String>("junk_params")
	val navParams = array<String>("nav_params")
}

internal fun ResultRow.toHost() = Host(
	id = this[HostTable.id].value,
	nexusId = this[HostTable.nexusId]?.value,
	core = this[HostTable.core],
	name = this[HostTable.name],
	logo = this[HostTable.logo],
	robotsTxt = this[HostTable.robotsTxt],
	isRedirect = this[HostTable.isRedirect],
	score = this[HostTable.score],
	bannedPaths = this[HostTable.bannedPaths].toSet(),
	domains = this[HostTable.domains].toSet(),
	junkParams = this[HostTable.junkParams].toSet(),
	navParams = this[HostTable.navParams].toSet(),
)

internal fun HostTable.findByCore(core: String): Host? {
	return this.select(HostTable.columns)
		.where { HostTable.core.sameAs(core) }
		.firstOrNull()?.toHost()
}