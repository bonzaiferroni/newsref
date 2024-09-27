package newsref.krawly

import newsref.db.Log
import newsref.db.initDb
import newsref.db.services.SourceService
import java.io.File

private val lead = "https://www.politico.com/news/2024/09/26/newsom-signs-reparations-apology-00181368"

suspend fun main(args: Array<String>) {
    initDb()
    val article = read(lead)
    SourceService().consume(article)
    val md = article.toMarkdown()
        ?: return
    val file = File("dump/markdown5.md")
    file.writeText(md)
}
