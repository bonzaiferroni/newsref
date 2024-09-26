package newsref.krawly

import newsref.db.initDb
import newsref.db.services.SourceService
import java.io.File

suspend fun main(args: Array<String>) {
    initDb()
    val article = read("https://fortune.com/2024/09/24/donald-trump-truth-social-tmtg-share-price/")
        ?: return
    SourceService().consume(article)
    val md = article.toMarkdown()
        ?: return
    val file = File("dump/markdown3.md")
    file.writeText(md)
}
