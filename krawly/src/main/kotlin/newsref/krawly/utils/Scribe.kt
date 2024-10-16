package newsref.krawly.utils

import kotlinx.serialization.json.JsonNull.content
import net.sourceforge.htmlunit.corejs.javascript.tools.debugger.Dim
import newsref.db.models.FetchInfo
import newsref.db.models.PageInfo

fun FetchInfo.toMarkdown(): String? {
    val page = this.page ?: return null
    val sb = StringBuilder()
    val contents = page.contents.joinToString("\n\n") { it }
    val externalLinksCount = page.links.count { it.isExternal }
    sb.append("# ${page.article.headline}\n${page.pageUrl}")
    sb.append("\nby ${page.authors?.joinToString(", ")}")
    sb.append("\n${this.source.type} - ${page.articleType} - $externalLinksCount/${page.links.size} links")
    sb.append(" - ${page.article.wordCount}")
    sb.append("\n\n$contents")
    var content = sb.toString()
    for (link in page.links) {
        content = content.replaceFirst(link.anchorText, "[${link.anchorText}](${link.url})")
    }
    return content
}