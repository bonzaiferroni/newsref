package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.model.CrawlInfo
import newsref.db.model.CrawledLink
import newsref.db.services.HostService
import newsref.db.services.NexusService

class NexusFinder(
	private val nexusService: NexusService = NexusService(),
	private val hostService: HostService = HostService(),
) {
	private val console = globalConsole.getHandle("NexusFinder")

	suspend fun findNexuses(crawl: CrawlInfo): CrawlInfo {
		val page = crawl.page ?: return crawl
		val pageHost = page.pageHost
		val links = mutableListOf<CrawledLink>()
		for (link in page.links) {
			if (!link.isExternal) {
				links.add(link)
				continue
			}
			if (knownNexuses.contains(link.url.domain)) {
				links.add(link.copy(isExternal = false))
				continue
			}
			val linkHost = hostService.findByUrl(link.url) ?: continue
			val nexus = nexusService.updateNexus(pageHost, linkHost)
			if (nexus != null) {
				console.logTrace("Nexus: ${nexus.name}")
				links.add(link.copy(isExternal = false))
			} else {
				links.add(link)
			}
		}
		return crawl.copy(page = crawl.page!!.copy(links = links))
	}
}

val knownNexuses = setOf(
	"login.politicopro.com",
	"subscriber.politcopro.com"
)