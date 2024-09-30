package newsref.krawly.agents

import newsref.db.services.OutletService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.data.Outlet
import newsref.model.utils.getApexDomain
import newsref.model.utils.removeQueryParameters

class OutletAgent(
    private val web: SpiderWeb,
    private val outletService: OutletService = OutletService()
) {
    suspend fun isAllowed(url: String): Boolean {
        val outlet = getOutlet(url)
        val path = url.getPath()
        val disallowedPaths = outlet.disallowed ?: return true
        for (disallowedPath in disallowedPaths) {
            if (path.startsWith(disallowedPath, true)) {
                println("🤖⛔ Nay: $url")
                return false
            }
        }
        return true
    }

    suspend fun removeParameters(url: String): String {
        val outlet = getOutlet(url)
        val urlParams = outlet.urlParams.toList()
        return url.removeQueryParameters(urlParams)
    }

    suspend fun getOutlet(url: String): Outlet {
        val apex = url.getApexDomain()
        return outletService.findByApex(apex) ?: createOutlet(url)
    }

    private suspend fun createOutlet(url: String): Outlet {
        val result = url.getRobotsTxtUrl()?.let { web.crawlPage(it) }
        val robotsTxt = result?.let{ if (it.isSuccess()) it.content else null }
        val disallowed = robotsTxt?.let { parseRobotsTxt(it) }
        val apex = url.getApexDomain()
        return outletService.createOutlet(apex, robotsTxt, disallowed)
    }
}

