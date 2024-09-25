package newsref.krawly

import it.skrape.core.htmlDocument
import newsref.db.initDb
import newsref.db.services.ArticleService
import java.io.File

suspend fun main(args: Array<String>) {
    initDb()
    val article = read("https://fortune.com/2024/09/24/donald-trump-truth-social-tmtg-share-price/")
    ArticleService().create(article)
    val file = File("markdown1.md")
    file.writeText(article.toMarkdown())
}