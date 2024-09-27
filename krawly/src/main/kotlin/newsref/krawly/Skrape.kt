package newsref.krawly

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc

fun skrapeDoc(url: String): Doc =
    skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request {
            this.url = url
            timeout = 30000
            userAgent = firefoxAgent
        }
        response {
            htmlDocument { this }
        }
    }

fun readDoc(html: String): Doc = htmlDocument(html)