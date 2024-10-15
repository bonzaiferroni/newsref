package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.services.HostService
import newsref.krawly.SpiderWeb
import newsref.db.log.toBlue
import newsref.krawly.utils.*
import newsref.model.core.*
import newsref.model.data.Host

class HostAgent(
	private val web: SpiderWeb,
	private val hostService: HostService = HostService()
) {
    private val console = globalConsole.getHandle("HostAgent")

    suspend fun getHost(url: Url): Pair<Host, CheckedUrl> {
        val host = hostService.findByUrl(url) ?: return createHost(url)
        if (host.isRedirect == true) {
            val redirectUrl = fetchRedirectUrl(url)
            if (redirectUrl != null && redirectUrl.core != url.core) {
                return getHost(redirectUrl)
            }
        }
        return Pair(host, url.href.toCheckedUrl(host))
    }

    private suspend fun createHost(url: Url): Pair<Host, CheckedUrl> {
        console.logPartial(url.domain.toBlue())

        val redirectUrl = fetchRedirectUrl(url)
        if (redirectUrl != null && redirectUrl.core != url.core) {
            console.finishPartial("found redirect: \n${redirectUrl}")
            hostService.createHost(
                url = url, robotsTxt = null, isRedirect = true, bannedPaths = emptySet()
            )
            return createHost(redirectUrl)
        }

        val robotsUrl = url.getRobotsTxtUrl()
        val result = web.fetch(robotsUrl, false)

        val robotsTxt = result.let{ if (it.isSuccess()) it.doc?.text else null }
        val disallowed = robotsTxt?.let { parseRobotsTxt(it) } ?: emptySet()
        console.finishPartial("${disallowed.size} disallowed: ${disallowed.take(3)}")
        val host = hostService.createHost(
            url = url, robotsTxt = robotsTxt, isRedirect = false, bannedPaths = disallowed
        )

        return Pair(host, url.href.toCheckedUrl(host))
    }

    private fun fetchRedirectUrl(url: Url) =
        web.fetchRedirect(url).takeIf { it.isRedirect() }?.redirectHref?.toUrlOrNull()
}

