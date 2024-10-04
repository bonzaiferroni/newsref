package newsref.krawly

import kotlinx.coroutines.delay
import newsref.db.initDb
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.db.services.OutletService
import newsref.krawly.agents.OutletAgent
import newsref.krawly.log.*
import newsref.krawly.log.oceanBlue
import newsref.krawly.log.sunsetOrange
import newsref.krawly.log.toAnsiForeground

suspend fun main(args: Array<String>) {
    val console = LogConsole(showStatus = args.contains("showStatus"))
    val handle = console.getHandle("main")
    val otherHandle = console.getHandle("other")
    handle.logInfo(args[0])
    handle.logTrace("hello world".toBlueBg())
    handle.logDebug("hello world".toGreenBg())
    handle.logInfo("hello world".toGrayBg())
    handle.logWarning("hello world".toPurpleBg())
    handle.logError("hello world".toForestBg())
    while (true) {
        delay(1000)
        handle.logInfo("hello world", "a")
        delay(1000)
        handle.logInfo("hello world".bold(), "🕷")
        delay(1000)
        handle.logInfo("hello world", "🕸")
    }
    // initDb()
    // val spider = Spider(SpiderWeb())
    // spider.startCrawling()
}
