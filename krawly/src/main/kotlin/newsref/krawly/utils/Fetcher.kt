package newsref.krawly.utils

import it.skrape.core.htmlDocument
import it.skrape.selects.Doc
import it.skrape.selects.ElementNotFoundException

fun fetch(url: String): Doc? {
    print("Fetcher: fetching... ")
    val html = pwFetch(url)
    if (html?.content == null) return null
    println("done. reading...")
    return contentToDoc(html.content)
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