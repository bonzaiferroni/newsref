package newsref.krawly

import newsref.model.core.Article

fun Article.toMarkdown(): String {
    var content = "# ${this.title}\n\n${this.content}"
    this.sources.forEach {
        println("[${it.text}](${it.url})")
        content = content.replace(it.text, "[${it.text}](${it.url})")
    }
    return content
}