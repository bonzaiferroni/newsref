package newsref.krawly.agents

import kotlinx.coroutines.delay
import newsref.db.services.OutletService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.core.Url
import newsref.model.core.toCheckedUrl
import newsref.model.data.Outlet

class OutletAgent(
    private val web: SpiderWeb,
    private val outletService: OutletService = OutletService()
) {
    suspend fun getOutlet(url: Url): Outlet {
        return outletService.findByHost(url.host)                               // <- OutletService
            ?: createOutlet(url)
    }

    suspend fun getAndSetName(url: Url, name: String?): Outlet {
        getOutlet(url)
        return outletService.findAndSetName(url, name)                          // <- OutletService ->
    }

    private suspend fun createOutlet(url: Url): Outlet {
        print("createOutlet: ${url.host}")
        val robotsUrl = url.getRobotsTxtUrl()
        var result = web.crawlPage(robotsUrl, false)                            // <- Web
        delay((1000..5000L).random())

        val robotsTxt = result?.let{ if (it.isSuccess()) it.doc?.text else null }
        val disallowed = robotsTxt?.let { parseRobotsTxt(it) } ?: emptySet()
        print(", ${disallowed.size} disallowed")
        if (disallowed.contains("/")) print("(everything)")
        val urlWithoutParams = url.toString().toCheckedUrl(emptySet(), null)
        delay((1000..5000L).random())
        result = web.crawlPage(urlWithoutParams, false)                         // <- Web

        val keepParams = if (result != null && result.isSuccess()) { emptySet() } else {
            url.params.map { it.key }.toSet()
        }
        println(", keepParams: $keepParams")
        return outletService.createOutlet(
            host = url.host, robotsTxt = robotsTxt, disallowed = disallowed, keepParams = keepParams
        )                                                                       //    OutletService ->
    }
}

