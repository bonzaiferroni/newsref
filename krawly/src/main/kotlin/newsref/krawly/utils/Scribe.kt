package newsref.krawly.utils

import newsref.model.dto.SourceInfo

fun SourceInfo.toMarkdown(): String? {
    val article = this.article ?: return null
    val contents = this.contents.joinToString("\n\n") { it }
    var content = "# ${article.headline}\n\n${contents}"
    this.links.forEach {
        content = content.replace(it.urlText, "[${it.urlText}](${it.url})")
    }
    return content
}