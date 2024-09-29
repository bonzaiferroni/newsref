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
//private val lead = "https://www.axios.com/2024/09/27/hurricane-helene-floods-florida-georgia-north-carolina"
//private val lead = "https://www.npr.org/2024/09/28/nx-s1-5132035/death-toll-from-hurricane-helene-mounts-as-aftermath-assessment-begins"
//private val lead = "https://apnews.com/article/trump-wisconsin-immigration-visit-arrest-0810efccefc8b094756d0c46f927b7dc"
//private val lead = "https://apnews.com/article/trump-wisconsin-immigration-visit-arrest-0810efccefc8b094756d0c46f927b7dc"
//private val lead = "https://www.timesofisrael.com/liveblog_entry/senior-iaf-officials-say-strike-that-killed-nasrallah-pulled-off-flawlessly/"
private val lead = "https://www.wsj.com/world/middle-east/israel-brings-fight-to-beirut-still-assessing-whether-hezbollahsleader-is-dead-1bf0d098?utm_source=newsshowcase&utm_medium=gnews&utm_campaign=CDAqDwgAKgcICjDW3MkBMOfLFTDviaED&utm_content=rundown&gaa_at=la&gaa_n=AWsEHT7oTp0Z9eTgDxlhXddL3bC-LDZOQ9pMtWRNIzRdRFiILdmocrFV8chs6fHZjACOL-yEfwRnJF3CH3nB&gaa_ts=66f87e5c&gaa_sig=fNwwaCusay9JFIikisdv5y-50FdG5wKnu0cYOYvziM-Co43SXuiMQn9caTeABLZ_onE9kuBZH4pNTDiFDeIOmA%3D%3D"

suspend fun main(args: Array<String>) {
    initDb()
    val article = read(lead)
    SourceService().consume(article)
    val md = article.toMarkdown()
        ?: return
    md.cacheResource(article.source.url, "md")
}
