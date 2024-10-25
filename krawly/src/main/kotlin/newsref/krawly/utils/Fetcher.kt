package newsref.krawly.utils

import it.skrape.core.htmlDocument
import it.skrape.selects.Doc
import it.skrape.selects.ElementNotFoundException

fun String.query(selector: String) = htmlDocument(this) {
    findFirst(selector).text
}