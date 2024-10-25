package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.HostRow
import newsref.db.tables.NexusRow
import newsref.db.tables.toData
import newsref.model.core.toUrl
import newsref.model.data.Host
import newsref.model.data.Nexus
import org.jetbrains.exposed.dao.flushCache
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and

class NexusService : DbService() {

	suspend fun createNexus(hostCore: String, otherCore: String) = dbQuery {
		val host = HostRow.findByCore(hostCore) ?: return@dbQuery null
		val other = HostRow.findByCore(otherCore) ?: return@dbQuery null
		val nexus = combineNexus(host, other) ?: combineNexus(other, host)
		if (nexus != null) return@dbQuery nexus

		val nexusRow = NexusRow.new { fromData(Nexus(name = "${host.core} ❤ ${other.core}")) }
		host.nexus = nexusRow
		other.nexus = nexusRow
		nexusRow.toData()
	}

	private fun combineNexus(first: HostRow, second: HostRow): Nexus? {
		val nexus = first.nexus ?: return null
		if (nexus == second.nexus) return null
		if (second.nexus != null) return null // todo: combine two existing nexuses
		second.nexus = nexus
		nexus.name = "${nexus.name} ❤ ${second.core}"
		return nexus.toData()
	}

	suspend fun updateNexus(pageHost: Host, linkHost: Host) = dbQuery {
		val host = HostRow.findByCore(pageHost.core) ?: return@dbQuery null
		val other = HostRow.findByCore(linkHost.core) ?: return@dbQuery null
		if (host.nexus?.id?.value != null && host.nexus?.id?.value == other.nexus?.id?.value) return@dbQuery null
		val hostSet = getHostCoreSet(pageHost)
		val linkSet = getHostCoreSet(linkHost)
		if (hostSet.any { hostCore ->
				linkSet.any { linkCore -> hostCore.contains(linkCore) || linkCore.contains(hostCore) }
			}) {
			createNexus(pageHost.core, linkHost.core)
		} else {
			null
		}
	}

	private suspend fun getHostCoreSet(host: Host) = dbQuery {
		host.nexusId?.let { HostRow.find { HostTable.nexusId eq it } }?.map { it.core }?.toSet()
			?: setOf(host.core)
	}
}
