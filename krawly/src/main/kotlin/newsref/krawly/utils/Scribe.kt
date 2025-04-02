package newsref.krawly.utils

import newsref.db.model.CrawlInfo

fun CrawlInfo.toMarkdown(): String? {
    val crawledData = this.crawledData ?: return null
    val sb = StringBuilder()
    val contents = crawledData.contents.joinToString("\n\n") { it }
    val externalLinksCount = crawledData.links.count { it.isExternal }
    sb.append("# ${crawledData.page.headline}\n${crawledData.page.url}")
    sb.append("\nby ${crawledData.authors?.joinToString(", ")}")
    sb.append("\n${crawledData.page.type} - ${crawledData.articleCategory} - $externalLinksCount/${crawledData.links.size} links")
    sb.append(" - ${crawledData.page.wordCount}")
    sb.append("\n\n$contents")
    var content = sb.toString()
    for (link in crawledData.links) {
        content = content.replaceFirst(link.anchorText, "[${link.anchorText}](${link.url})")
    }
    return content
}