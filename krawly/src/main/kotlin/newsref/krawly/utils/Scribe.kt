package newsref.krawly.utils

import newsref.model.dto.DocumentInfo

fun DocumentInfo.toMarkdown(): String {
    val contents = this.contents.joinToString("\n\n") { it }
    var content = "# ${article.headline}\n\n${contents}"
    this.links.forEach {
        content = content.replace(it.anchorText, "[${it.anchorText}](${it.url})")
    }
    return content
}