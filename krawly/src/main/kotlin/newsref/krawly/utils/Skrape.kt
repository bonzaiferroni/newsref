package newsref.krawly.utils

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url

fun Url.skrapeDoc(): Doc =
    skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request {
            this.url = this.toString()
            timeout = 30000
            userAgent = chromeLinuxAgent
        }
        response {
            htmlDocument { this }
        }
    }

fun String.contentToDoc(): Doc = htmlDocument(this)