package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.models.CrawlInfo
import newsref.db.models.PageLink
import newsref.db.services.HostService
import newsref.db.services.NexusService
import kotlin.time.measureTime

class NexusFinder(
	private val nexusService: NexusService = NexusService(),
	private val hostService: HostService = HostService(),
) {
	private val console = globalConsole.getHandle("NexusFinder")

	suspend fun findNexuses(crawl: CrawlInfo): CrawlInfo {
		val page = crawl.page ?: return crawl
		val pageHost = page.pageHost
		val links = mutableListOf<PageLink>()
		for (link in page.links) {
			if (!link.isExternal) {
				links.add(link)
				continue
			}
			val linkHost = hostService.findByUrl(link.url) ?: continue
			if (pageHost.core.contains(linkHost.core)) {
				// console.log("ey!")
			}
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