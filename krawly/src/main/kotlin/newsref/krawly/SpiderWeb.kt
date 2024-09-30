package newsref.krawly

import newsref.krawly.utils.pwFetch

class SpiderWeb {
    fun crawlPage(url: String, screenshot: Boolean = false) = pwFetch(url, screenshot)
}