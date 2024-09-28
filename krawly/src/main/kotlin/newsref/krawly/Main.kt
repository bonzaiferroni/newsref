package newsref.krawly

import newsref.db.Log
import newsref.db.initDb
import newsref.db.services.SourceService
import java.io.File

//private val lead = "https://arh.antoinevastel.com/bots/areyouheadless"
//private val lead = "https://www.politico.com/news/2024/09/26/newsom-signs-reparations-apology-00181368"
private val lead = "https://www.theatlantic.com/newsletters/archive/2024/09/books-briefing-millennials-gen-x-youth-intermezzo/680048/"

suspend fun main(args: Array<String>) {
    initDb()
    val article = read(lead)
    SourceService().consume(article)
    val md = article.toMarkdown()
        ?: return
    val file = File("dump/lastpage.md")
    file.writeText(md)
}
