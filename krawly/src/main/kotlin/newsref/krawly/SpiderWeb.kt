package newsref.krawly

import newsref.krawly.utils.pwFetch
import newsref.krawly.utils.pwFetchHead
import newsref.krawly.utils.pwFetchNoRedirect
import newsref.model.core.Url

class SpiderWeb {
    fun fetch(url: Url, screenshot: Boolean = false) = pwFetch(url, screenshot)
    fun fetchHead(url: Url) = pwFetchHead(url)
    fun fetchNoRedirect(url: Url, screenshot: Boolean = false) = pwFetchNoRedirect(url, screenshot)
}