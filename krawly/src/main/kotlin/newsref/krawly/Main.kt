package newsref.krawly

import kotlinx.coroutines.delay
import newsref.db.globalConsole
import newsref.db.initDb
import newsref.db.log.ConsoleConfig
import newsref.db.utils.cacheResource
import newsref.db.utils.cacheSerializable
import newsref.krawly.agents.*
import newsref.krawly.utils.pwFetch
import newsref.model.core.Url
import newsref.model.core.toUrl
import kotlin.time.Duration.Companion.minutes

suspend fun main(args: Array<String>) {
	println(args.joinToString(", "))
	if (args.any {it.contains("http")}) {
		test(args.first())
	} else {
		globalConsole.config = ConsoleConfig(showStatus = args.contains("showStatus"))
		crawl()
	}
}

suspend fun crawl() {
	initDb()
	val web = SpiderWeb()
	val leadMaker = LeadMaker()
	val outletAgent = OutletAgent(web)
	val feedChecker = FeedChecker(web, outletAgent, leadMaker)
	val leadFollower = LeadFollower(web, leadMaker, outletAgent)
	feedChecker.start()
	leadFollower.start()
	while (true) {
		// don't exit
		delay(10.minutes)
	}
	// val spider = Spider(SpiderWeb(), console)
	// spider.startCrawling()
}

fun test(href: String) {
	val url = href.toUrl()
	val result = pwFetch(url, true)
	result?.screenshot?.cacheResource(url, "png", "test/${url.host}")
	result?.requestHeaders?.map { "${it.key}:\n${it.value}" }?.joinToString("\n\n")?.cacheResource(url, "txt", "test/${url.host}", "headers")
}
