package newsref.krawly.utils

import it.skrape.core.htmlDocument
import it.skrape.fetcher.*
import it.skrape.selects.Doc
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url

fun skrapeDocJs(url: Url): Doc =
    skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request {
            this.url = url.toString()
            timeout = 30000
            headers = extraHeaders
            userAgent = chromeLinuxAgent
        }
        response {
            htmlDocument { this }
        }
    }

fun String.contentToDoc(): Doc = htmlDocument(this)