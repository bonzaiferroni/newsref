package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.HostRow
import newsref.db.tables.NexusRow
import newsref.db.tables.toModel
import newsref.db.model.Host
import newsref.db.model.Nexus

class NexusService : DbService() {

	suspend fun createNexus(hostCore: String, otherCore: String) = dbQuery {
		val host = HostRow.findByCore(hostCore) ?: return@dbQuery null
		val other = HostRow.findByCore(otherCore) ?: return@dbQuery null
		val nexus = combineNexus(host, other) ?: combineNexus(other, host)
		if (nexus != null) return@dbQuery nexus

		val nexusRow = NexusRow.new { fromModel(Nexus(name = "${host.core} ❤ ${other.core}")) }
		host.nexus = nexusRow
		other.nexus = nexusRow
		nexusRow.toModel()
	}

	private fun combineNexus(first: HostRow, second: HostRow): Nexus? {
		val nexus = first.nexus ?: return null
		if (nexus == second.nexus) return null
		if (second.nexus != null) return null // todo: combine two existing nexuses
		second.nexus = nexus
		nexus.name = "${nexus.name} ❤ ${second.core}"
		return nexus.toModel()
	}

	suspend fun updateNexus(pageHost: Host, linkHost: Host) = dbQuery {
		val host = HostRow.findByCore(pageHost.core) ?: return@dbQuery null
		val other = HostRow.findByCore(linkHost.core) ?: return@dbQuery null
		if (host.nexus?.id?.value != null && host.nexus?.id?.value == other.nexus?.id?.value) return@dbQuery null
		val hostSet = getHostCoreSet(pageHost)
		val linkSet = getHostCoreSet(linkHost)
		if (hostSet.any { hostCore ->
				linkSet.any { linkCore -> hostCore.endsWith(".${linkCore}")
						|| linkCore.endsWith(".${hostCore}") }
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
