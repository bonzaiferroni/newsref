package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import newsref.db.globalConsole
import newsref.db.services.HostService
import newsref.krawly.SpiderWeb
import newsref.model.core.*
import newsref.model.data.Host
import kotlin.time.Duration.Companion.seconds

class HostAgent(
	private val web: SpiderWeb,
	private val hostService: HostService = HostService()
) {
    private val console = globalConsole.getHandle("HostAgnt")

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

    private val mutex = Mutex()
    private var creatingHost: String? = null
    private suspend fun createHost(url: Url): Pair<Host, CheckedUrl> {
        mutex.withLock {
            if (url.domain == creatingHost) {
                while (url.domain == creatingHost) {
                    delay(1.seconds)
                }
                return getHost(url)
            }
            creatingHost = url.domain
        }

        val host = hostService.createHost(
            url = url, robotsTxt = null, isRedirect = false, bannedPaths = emptySet()
        )

        mutex.withLock {
            creatingHost = null
        }

        return Pair(host, url.href.toCheckedUrl(host))
    }

    private fun fetchRedirectUrl(url: Url) =
        web.fetchRedirect(url).takeIf { it.isRedirect() }?.redirectHref?.toUrlOrNull()
}

//        console.logPartial(url.domain.toBlue())

//        val redirectUrl = fetchRedirectUrl(url)
//        if (redirectUrl != null && redirectUrl.core != url.core) {
//            console.finishPartial("found redirect: \n${redirectUrl}")
//            val host = hostService.findByUrl(url)
//            if (host == null) {
//                hostService.createHost(
//                    url = url, robotsTxt = null, isRedirect = true, bannedPaths = emptySet()
//                )
//            }
//            return createHost(redirectUrl)
//        }

//        val robotsUrl = url.getRobotsTxtUrl()
//        val result = web.fetch(robotsUrl, false)

//        val robotsTxt = result.let{ if (it.isSuccess()) it.doc?.text else null }
//        val disallowed = robotsTxt?.let { parseRobotsTxt(it) } ?: emptySet()
//        val everythingDisallowed = disallowed.contains("/")
//        console.finishPartial("${disallowed.size} disallowed ${if (everythingDisallowed) "(everything)" else ""}")

