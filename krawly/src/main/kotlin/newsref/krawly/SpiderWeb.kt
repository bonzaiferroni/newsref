package newsref.krawly

import newsref.krawly.utils.pwFetch
import newsref.krawly.utils.pwFetchRedirect
import newsref.model.core.Url

class SpiderWeb {
    fun fetch(url: Url, screenshot: Boolean = false) = pwFetch(url, screenshot)
    fun fetchRedirect(url: Url) = pwFetchRedirect(url)
}