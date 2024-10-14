package newsref.krawly.utils

import newsref.db.models.PageInfo

fun PageInfo.toMarkdown(): String {
    val contents = this.contents.joinToString("\n\n") { it }
    var content = "# ${article.headline}\n\n${contents}"
    for (link in this.links) {
        content = content.replaceFirst(link.anchorText, "[${link.anchorText}](${link.url})")
    }
    return content
}