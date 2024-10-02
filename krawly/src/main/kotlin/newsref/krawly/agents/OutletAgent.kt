package newsref.krawly.agents

import com.eygraber.uri.Uri
import com.eygraber.uri.Url
import kotlinx.coroutines.delay
import newsref.db.services.OutletService
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.data.Outlet
import newsref.model.utils.removeQueryParameters
import newsref.model.utils.tryParseUntrustedUrl

class OutletAgent(
    private val web: SpiderWeb,
    private val outletService: OutletService = OutletService()
) {
    suspend fun getTrustedUrl(href: String, context: Url?): Url? {
        val untrustedUrl = href.tryParseUntrustedUrl(null, context) ?: return null
        val outlet = outletService.findByHost(untrustedUrl.host)
            ?: createOutlet(untrustedUrl)

        val url = href.tryParseUntrustedUrl(outlet.urlParams, null) ?: return null
        val path = url.path ?: "/"
        val disallowedPaths = outlet.disallowed ?: return url
        for (disallowedPath in disallowedPaths) {
            if (path.startsWith(disallowedPath, true)) {
                println("🤖⛔ Nay: $url")
                return null
            }
        }
        return url
    }

    suspend fun getOutlet(url: Url): Outlet? {
        return outletService.findByHost(url.host)                               // <- OutletService
    }

    suspend fun findAndSetName(url: Url, name: String?): Outlet {
        return outletService.findAndSetName(url, name)                          // <- OutletService ->
    }

    private suspend fun createOutlet(untrustedUrl: Url): Outlet {
        print("createOutlet: ${untrustedUrl.host}")
        val robotsUrl = untrustedUrl.getRobotsTxtUrl()
        var result = web.crawlPage(robotsUrl, false)                            // <- Web
        val robotsTxt = result?.let{ if (it.isSuccess()) it.doc?.text else null }
        val disallowed = robotsTxt?.let { parseRobotsTxt(it) } ?: emptySet()
        val urlWithoutParams = untrustedUrl.toString().tryParseUntrustedUrl(emptySet(), null)!!
        delay((1000..5000L).random())
        result = web.crawlPage(urlWithoutParams, false)                         // <- Web
        delay((1000..5000L).random())
        val keepParams = if (result != null && result.isSuccess()) { emptySet() } else {
            untrustedUrl.getQueryParameterNames()
        }
        println("keepParams: $keepParams")
        return outletService.createOutlet(
            host = untrustedUrl.host, robotsTxt = robotsTxt, disallowed = disallowed, keepParams = keepParams
        )                                                                       //    OutletService ->
    }
}

