package newsref.krawly

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape

fun main(args: Array<String>) {
    val document = getDocumentByUrl("https://fortune.com/2024/09/24/donald-trump-truth-social-tmtg-share-price/")
    document.eachHref.forEach({println(it)})
}

fun getDocumentByUrl(urlToScrape: String) = skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
    request { url = urlToScrape }
    response { htmlDocument { this } }
}