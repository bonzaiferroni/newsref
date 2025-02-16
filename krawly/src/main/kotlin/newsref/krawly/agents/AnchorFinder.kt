package newsref.krawly.agents

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import newsref.db.globalConsole
import newsref.krawly.utils.findFirstOrNull
import newsref.db.model.Feed
import java.io.File

private val console = globalConsole.getHandle("AnchorFinder")

class AnchorFinder {

    fun fromContent(feed: Feed, content: String?): List<Element> {
        if (content == null) {
            console.logError("feed error: ${feed.url}")
            return emptyList()
        }

        if (feed.debug) {
            val path = "../cache/AnchorFinder/debug.html"
            val file = File(path)
            file.parentFile.mkdirs()
            file.writeText(content)
        }

        val doc = Ksoup.parse(content)

        val selector = feed.selector
        val elements = if (selector.isNullOrEmpty()) {
            (doc.selectOrNull("main a:not(nav a)") ?: doc.selectOrNull("a:not(nav a)"))
                ?.let { filterElements(it) }
        } else {
            doc.selectOrNull(selector)
        }
        if (elements == null) {
            console.logWarning("no leads with selector: ${feed.selector}\n${feed.url}")
            return emptyList()
        }
        return elements
    }

    private fun filterElements(list: List<Element>) = list.filter { anchor ->
        if (anchor.tagName() != "a") return@filter false
        val headline = anchor.text()
        if (headline.isBlank()) return@filter false
        val href = anchor.attribute("href")?.value
        if (href == null || isNotArticle(href)) return@filter false
        val level = findParentLevel(anchor) ?: findChildLevel(anchor)
        if (level == null) return@filter false
        true
    }

    fun fromScan(doc: Doc): List<DocElement> {
        val list = mutableListOf<DocElement>()
        val root = doc.findFirstOrNull("main") ?: doc.findFirst("body")
        scanElement(root, list)
        return list
    }

    private fun scanElement(element: DocElement, list: ElementList) {
        if (blockedTags.contains(element.tagName)) return
        if (element.tagName == "a") {
            list.add(element)
            return
        }
        for (child in element.children) {
            scanElement(child, list)
        }
    }
}

typealias ElementList = MutableList<DocElement>

fun Document.selectOrNull(selector: String) = try {
    val elements = this.select(selector)
    if (elements.isNotEmpty()) elements else null
} catch (e: Exception) {
    null
}

private fun isNotArticle(href: String): Boolean {
    if (blockedHrefStarts.any { href.startsWith(it) }) return true
    return blockedHrefContents.any { href.contains(it) }
}

private val blockedHrefStarts = setOf(
    "#", "{{"
)

private val blockedHrefContents = setOf(
    "newsletter"
)

private val blockedTags = setOf(
    "nav"
)

private fun findParentLevel(element: Element?): Int? {
    if (element == null) return null
    if (element.tagName() == "body") return null
    return element.tagName().toLevel() ?: findParentLevel(element.parent())
}

private fun findChildLevel(element: Element): Int? {
    for (child in element.children()) {
        val level = child.tagName().toLevel() ?: continue
        return level
    }
    for (child in element.children()) {
        val level = findChildLevel(child) ?: continue
        return level
    }
    return null
}

private fun String.toLevel() = when (this) {
    "h1" -> 1
    "h2" -> 2
    "h3" -> 3
    "h4" -> 4
    "h5" -> 5
    "p" -> 6
    else -> null
}