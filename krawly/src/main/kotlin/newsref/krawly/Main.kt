package newsref.krawly

import newsref.db.Log
import newsref.db.initDb
import newsref.db.services.SourceService
import newsref.db.utils.cacheResource
import newsref.model.utils.getApexDomain
import java.io.File

//private val lead = "https://arh.antoinevastel.com/bots/areyouheadless"
//private val lead = "https://www.politico.com/news/2024/09/26/newsom-signs-reparations-apology-00181368"
//private val lead = "https://www.theatlantic.com/newsletters/archive/2024/09/books-briefing-millennials-gen-x-youth-intermezzo/680048/"
//private val lead = "https://www.nytimes.com/2024/08/28/us/politics/trump-arlington-cemetery.html"
//private val lead = "https://www.reuters.com/world/middle-east/israel-continue-ceasefire-discussions-lebanon-netanyahu-says-2024-09-27/"
private val lead = "https://www.axios.com/2024/09/27/hurricane-helene-floods-florida-georgia-north-carolina"

suspend fun main(args: Array<String>) {
    initDb()
    val article = read(lead)
    SourceService().consume(article)
    val md = article.toMarkdown()
        ?: return
    md.cacheResource(article.source.url, "md")
}
