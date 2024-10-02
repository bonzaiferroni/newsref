package newsref.krawly

import newsref.krawly.utils.pwFetch
import newsref.model.core.Url

class SpiderWeb {
    fun crawlPage(url: Url, screenshot: Boolean = false) = pwFetch(url, screenshot)
}