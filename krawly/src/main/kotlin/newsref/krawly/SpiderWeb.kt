package newsref.krawly

import newsref.krawly.utils.*
import newsref.model.core.Url
import newsref.model.data.FetchStrategy

class SpiderWeb {
    fun fetch(url: Url, strategy: FetchStrategy) = when (strategy) {
        FetchStrategy.BASIC -> ktorFetch(url)
        FetchStrategy.BROWSER -> pwFetch(url)
    }
    fun fetch(url: Url, screenshot: Boolean = false) = pwFetch(url, screenshot)
    fun fetchRedirect(url: Url) = pwFetchRedirect(url)
}