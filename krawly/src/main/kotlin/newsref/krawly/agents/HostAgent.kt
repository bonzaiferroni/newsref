package newsref.krawly.agents

import kotlinx.coroutines.delay
import newsref.db.core.CheckedUrl
import newsref.db.core.Url
import newsref.db.core.toCheckedUrl
import newsref.db.core.toUrlOrNull
import newsref.db.globalConsole
import newsref.db.services.HostService
import newsref.krawly.SpiderWeb
import newsref.db.model.Host
import java.util.*
import kotlin.time.Duration.Companion.seconds

class HostAgent(
	private val web: SpiderWeb,
	private val hostService: HostService = HostService()
) {
    private val console = globalConsole.getHandle("HostAgnt")

    suspend fun getHost(hostId: Int): Host {
        return hostService.findById(hostId)
    }

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

    private val creatingHosts = Collections.synchronizedSet(mutableSetOf<String>())
    private suspend fun createHost(url: Url): Pair<Host, CheckedUrl> {
//        console.log("Creating Host: ${url.core}")
        if (url.core in creatingHosts) {
            while (url.core in creatingHosts) {
                delay(1.seconds)
            }
            return getHost(url)
        }
        creatingHosts.add(url.core)

        val host = hostService.createHost(
            url = url, robotsTxt = null, isRedirect = false, bannedPaths = emptySet()
        )

        creatingHosts.remove(url.core)

        return Pair(host, url.href.toCheckedUrl(host))
    }

    suspend fun updateParameters(host: Host, navParams: Set<String>?, junkParams: Set<String>?): Host {
        if (navParams == null && junkParams == null) return host
        return hostService.updateParameters(host.id, navParams, junkParams)
    }

    private fun fetchRedirectUrl(url: Url) =
        web.fetchRedirect(url).takeIf { it.isRedirect() }?.redirectHref?.toUrlOrNull()
}

