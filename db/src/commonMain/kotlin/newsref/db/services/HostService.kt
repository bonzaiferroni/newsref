package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.HostRow
import newsref.db.tables.fromData
import newsref.db.tables.toData
import newsref.db.utils.sameAs
import newsref.model.core.Url
import newsref.model.data.Host

class HostService : DbService() {
	suspend fun findByUrl(url: Url): Host? = dbQuery {
		HostRow.find { HostTable.core.sameAs(url.core) }.firstOrNull()?.toData()
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
}