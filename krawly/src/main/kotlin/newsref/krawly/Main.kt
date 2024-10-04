package newsref.krawly

import kotlinx.coroutines.delay
import newsref.db.initDb
import newsref.krawly.agents.FeedChecker
import newsref.krawly.agents.OutletAgent
import newsref.krawly.log.*
import kotlin.time.Duration.Companion.minutes

suspend fun main(args: Array<String>) {
	val console = LogConsole(showStatus = args.contains("showStatus"))
	initDb()
	val web = SpiderWeb()
	val outletAgent = OutletAgent(console, web)
	val feedChecker = FeedChecker(console, web, outletAgent)
	feedChecker.start()
	while (true) {
		// don't exit
		delay(10.minutes)
	}
	// val spider = Spider(SpiderWeb(), console)
	// spider.startCrawling()
}
