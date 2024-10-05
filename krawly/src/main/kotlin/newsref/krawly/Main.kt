package newsref.krawly

import kotlinx.coroutines.delay
import newsref.db.initDb
import newsref.krawly.agents.*
import newsref.krawly.log.*
import kotlin.time.Duration.Companion.minutes

var _console: LogConsole? = null
val console get() = _console!!

suspend fun main(args: Array<String>) {
	_console = LogConsole(showStatus = args.contains("showStatus"))
	initDb()
	val web = SpiderWeb()
	val leadMaker = LeadMaker()
	val outletAgent = OutletAgent(console, web)
	val feedChecker = FeedChecker(console, web, outletAgent, leadMaker)
	val leadFollower = LeadFollower(console, web, leadMaker, outletAgent)
	feedChecker.start()
	leadFollower.start()
	while (true) {
		// don't exit
		delay(10.minutes)
	}
	// val spider = Spider(SpiderWeb(), console)
	// spider.startCrawling()
}
