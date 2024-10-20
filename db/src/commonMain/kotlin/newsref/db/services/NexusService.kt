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
	suspend fun getHostLinkMap(sumThreshold: Int): Map<Host, Map<Host, Int>> = dbQuery {
		val hosts = HostRow.all().map { it.toData() }
		val hostMap = mutableMapOf<Host, Map<Host, Int>>()
		for (host in hosts) {
			val linkUrls = SourceTable.join(LinkTable, JoinType.LEFT, SourceTable.id, LinkTable.sourceId)
				.select(LinkTable.url)
				.where { (SourceTable.hostId eq host.id) and (LinkTable.isExternal eq true) }
				.map { it[LinkTable.url].toUrl() }

			val linkMap = mutableMapOf<Host, Int>()
			for (url in linkUrls) {
				val other = hosts.firstOrNull() { it.core == url.core } ?: continue
				linkMap[other] = linkMap.getOrPut(other) { 0 } + 1
			}
			hostMap[host] = linkMap
		}
		hostMap // return
	}

	suspend fun getHostCores(): Set<String> = dbQuery {
		HostTable.select(HostTable.core).map { it[HostTable.core] }.toSet()
	}

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
		if (nexus == second.nexus) return nexus.toData()
		// todo: combine nexuses
		if (second.nexus != null) return null
		second.nexus = nexus
		nexus.name = "${nexus.name} ❤ ${second.core}"
		return nexus.toData()
	}

	suspend fun removeNexus(hostId: Int, nexusId: Int) = dbQuery {
		val host = HostRow.findById(hostId) ?: return@dbQuery false
		if (host.nexus?.id?.value != nexusId) return@dbQuery false
		val nexus = NexusRow.findById(nexusId) ?: return@dbQuery false
		host.nexus = null
		flushCache()
		if (nexus.hosts.count() == 1L) {
			nexus.delete()
		} else {
			nexus.name = nexus.name.replaceFirst("${host.core} ❤ ", "")
				.replaceFirst(" ❤ ${host.core}", "")
		}
		true
	}
}