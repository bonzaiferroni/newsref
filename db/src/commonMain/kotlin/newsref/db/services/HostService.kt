package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.HostRow
import newsref.db.tables.fromData
import newsref.db.tables.toData
import newsref.db.utils.sameAs
import newsref.model.core.Url
import newsref.model.data.Host
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class HostService : DbService() {
	suspend fun findByUrl(url: Url): Host? = dbQuery {
		HostRow.find { HostTable.core.sameAs(url.core) }.firstOrNull()?.toData()
	}

	suspend fun findById(hostId: Int) = dbQuery {
		HostRow.find( HostTable.id eq hostId).firstOrNull()?.toData()
			?: throw IllegalArgumentException("Host $hostId not found")
	}

	suspend fun createHost(
		url: Url,
		robotsTxt: String?,
		isRedirect: Boolean,
		bannedPaths: Set<String>,
	) = dbQuery {
		val domain = url.domain
		val domains = mutableListOf(domain)
		if (domain.startsWith("www.")) domains.add(domain.removePrefix("www."))
		val host = Host(
			core = url.core,
			robotsTxt = robotsTxt,
			isRedirect = isRedirect,
			domains = domains.toSet(),
			bannedPaths = bannedPaths.toSet(),
		)
		HostRow.new { fromData(host) }.toData()
	}

	suspend fun updateParameters(host: Host, junkParams: Set<String>?, navParams: Set<String>?) = dbQuery {
		val hostRow = HostRow.find( HostTable.id eq host.id).firstOrNull()
			?: throw IllegalArgumentException("Host ${host.core} not found")
		junkParams?.let { hostRow.junkParams = it.smoosh(hostRow.junkParams) }
		navParams?.let { hostRow.navParams = it.smoosh(hostRow.navParams) }
		hostRow.toData()
	}
}

private fun <T> Set<T>.smoosh(list: List<T>) = this.toMutableSet().also { set -> set.addAll(list) }.toList()