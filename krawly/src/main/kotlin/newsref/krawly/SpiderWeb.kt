package newsref.krawly

import newsref.krawly.utils.*
import newsref.db.core.Url
import newsref.db.model.FetchStrategy

class SpiderWeb {
    suspend fun fetch(url: Url, strategy: FetchStrategy = FetchStrategy.BROWSER) = when (strategy) {
        FetchStrategy.BASIC -> ktorFetchAsync(url)
        FetchStrategy.BROWSER -> pwFetch(url)
    }
    fun fetchRedirect(url: Url) = pwFetchRedirect(url)
}