package newsref.krawly

import newsref.model.dto.SourceInfo

fun SourceInfo.toMarkdown(): String? {
    val document = this.document ?: return null
    val contents = this.contents.joinToString("\n\n") { it }
    var content = "# ${document.title}\n\n${contents}"
    this.links.forEach {
        content = content.replace(it.urlText, "[${it.urlText}](${it.url})")
    }
    return content
}