package newsref.krawly.utils

import newsref.model.dto.DocumentInfo

fun DocumentInfo.toMarkdown(): String {
    val contents = this.contents.joinToString("\n\n") { it }
    var content = "# ${article.headline}\n\n${contents}"
    for (link in this.links) {
        if (link.anchorText.isBlank()) continue
        content = content.replace(link.anchorText, "[${link.anchorText}](${link.url})")
    }
    return content
}