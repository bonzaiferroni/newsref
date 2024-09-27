package newsref.krawly

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import it.skrape.selects.ElementNotFoundException

fun fetch(url: String): Doc {
    print("Fetcher: fetching... ")
    val html = pwFetch(url)
    println("done. reading...")
    return readDoc(html)
}

fun String.query(selector: String) = htmlDocument(this) {
    findFirst(selector).text
}

fun String.tryQuery(selector: String) = try {
    query(selector)
} catch (e: ElementNotFoundException) {
    println(e.message)
    null
}