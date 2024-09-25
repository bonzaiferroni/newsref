package newsref.krawly

import newsref.model.dto.ArticleInfo

fun ArticleInfo.toMarkdown(): String {
    var content = "# ${this.source.title}\n\n${this.source.content}"
    this.links.forEach {
        println("[${it.urlText}](${it.url})")
        content = content.replace(it.urlText, "[${it.urlText}](${it.url})")
    }
    return content
}