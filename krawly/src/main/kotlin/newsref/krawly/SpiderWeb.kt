package newsref.krawly

import com.eygraber.uri.Url
import newsref.krawly.utils.pwFetch

class SpiderWeb {
    fun crawlPage(url: Url, screenshot: Boolean = false) = pwFetch(url, screenshot)
}