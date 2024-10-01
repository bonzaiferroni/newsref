package newsref.krawly.agents

import com.eygraber.uri.Url
import newsref.db.services.OutletService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.data.Outlet

class OutletAgent(
    private val web: SpiderWeb,
    private val outletService: OutletService = OutletService()
) {
    suspend fun isAllowed(url: Url): Boolean {
        val outlet = getOutlet(url)
        val path = url.path ?: "/"
        val disallowedPaths = outlet.disallowed ?: return true
        for (disallowedPath in disallowedPaths) {
            if (path.startsWith(disallowedPath, true)) {
                println("🤖⛔ Nay: $url")
                return false
            }
        }
        return true
    }

    suspend fun getOutlet(url: Url): Outlet {
        return outletService.findByHost(url) ?: createOutlet(url, null)         // <- OutletService
    }

    suspend fun findAndSetName(url: Url, name: String?): Outlet {
        return outletService.findAndSetName(url, name)
            ?: createOutlet(url, name)
    }

    private suspend fun createOutlet(url: Url, name: String?): Outlet {
        val robotsUrl = url.getRobotsTxtUrl()
        val result = web.crawlPage(robotsUrl)                                   // <- Web
        val robotsTxt = result?.let{ if (it.isSuccess()) it.content else null }
        val disallowed = robotsTxt?.let { parseRobotsTxt(it) }
        return outletService.createOutlet(url, robotsTxt, disallowed, name)     //    OutletService ->
    }
}

