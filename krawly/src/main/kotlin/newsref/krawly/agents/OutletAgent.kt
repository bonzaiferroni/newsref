package newsref.krawly.agents

import kotlinx.coroutines.delay
import newsref.db.globalConsole
import newsref.db.services.OutletService
import newsref.krawly.SpiderWeb
import newsref.db.log.LogConsole
import newsref.db.log.LogLevel
import newsref.krawly.utils.*
import newsref.model.core.Url
import newsref.model.core.toCheckedUrl
import newsref.model.data.Outlet
import kotlin.time.Duration.Companion.seconds

class OutletAgent(
	private val web: SpiderWeb,
	private val outletService: OutletService = OutletService()
) {
    private val console = globalConsole.getHandle("OutletAgent")

    suspend fun getOutlet(url: Url): Outlet {
        return outletService.findByHost(url.host)                               // <- OutletService
            ?: createOutlet(url)
    }

    private suspend fun createOutlet(url: Url): Outlet {
        console.logPartial("create: ${url.host}")
        val robotsUrl = url.getRobotsTxtUrl()
        var result = web.crawlPage(robotsUrl, false)                            // <- Web

        val robotsTxt = result?.let{ if (it.isSuccess()) it.doc?.text else null }
        val disallowed = robotsTxt?.let { parseRobotsTxt(it) } ?: emptySet()
        console.logPartial(", ${disallowed.size} disallowed: ${disallowed.take(5)}")
        val urlWithoutParams = url.toString().toCheckedUrl(emptySet(), null)

        delay((1..2).random().seconds)
        result = web.crawlPage(urlWithoutParams, false)                         // <- Web

        val keepParams = if (result != null && result.isSuccess()) { emptySet() } else {
            url.params.map { it.key }.toSet()
        }
        console.finishPartial(", keepParams: ${keepParams.take(5)}", LogLevel.DEBUG)

        return outletService.createOutlet(
            host = url.host, robotsTxt = robotsTxt, disallowed = disallowed, keepParams = keepParams
        )                                                                       //    OutletService ->
    }
}

