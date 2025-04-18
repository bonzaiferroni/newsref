package newsref.db.services

import klutch.db.DbService
import klutch.db.read
import klutch.db.readById
import klutch.db.updateById
import newsref.db.tables.*
import newsref.db.model.Host
import newsref.db.model.Nexus
import org.jetbrains.exposed.sql.insertAndGetId

class NexusService : DbService() {

    suspend fun createNexus(hostCore: String, otherCore: String) = dbQuery {
        val host = HostTable.findByCore(hostCore) ?: return@dbQuery null
        val other = HostTable.findByCore(otherCore) ?: return@dbQuery null
        val nexus = combineNexus(host, other) ?: combineNexus(other, host)
        if (nexus != null) return@dbQuery nexus

        val nexusId = NexusTable.insertAndGetId {
            it[this.name] = "${host.core} ❤ ${other.core}"
        }.value
        HostTable.updateById(host.id) {
            it[this.nexusId] = nexusId
        }
        HostTable.updateById(other.id) {
            it[this.nexusId] = nexusId
        }

        NexusTable.readById(nexusId).toNexus()
    }

    private fun combineNexus(first: Host, second: Host): Nexus? {
        val nexusId = first.nexusId ?: return null
        if (nexusId == second.nexusId) return null
        if (second.nexusId != null) return null // todo: combine two existing nexuses
        HostTable.updateById(second.id) {
            it[this.nexusId] = nexusId
        }
        val nexus = NexusTable.readById(nexusId).toNexus()
        NexusTable.updateById(nexusId) {
            it[this.name] = "${nexus.name} ❤ ${second.core}"
        }
        return NexusTable.readById(nexusId).toNexus()
    }

    suspend fun updateNexus(pageHost: Host, linkHost: Host) = dbQuery {
        val host = HostTable.findByCore(pageHost.core) ?: return@dbQuery null
        val other = HostTable.findByCore(linkHost.core) ?: return@dbQuery null
        if (host.nexusId != null && host.nexusId == other.nexusId) return@dbQuery null
        val hostSet = getHostCoreSet(pageHost)
        val linkSet = getHostCoreSet(linkHost)
        if (hostSet.any { hostCore ->
                linkSet.any { linkCore ->
                    hostCore.endsWith(".${linkCore}")
                            || linkCore.endsWith(".${hostCore}")
                }
            }) {
            createNexus(pageHost.core, linkHost.core)
        } else {
            null
        }
    }

    private suspend fun getHostCoreSet(host: Host) = dbQuery {
        host.nexusId?.let { nexusId ->
            HostTable.read(listOf(HostTable.core)) { HostTable.nexusId.eq(nexusId) }
                .map { it[HostTable.core] }
                .takeIf { it.isNotEmpty() }?.toSet()
        } ?: setOf(host.core)
    }
}
