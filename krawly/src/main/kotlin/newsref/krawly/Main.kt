package newsref.krawly

import newsref.db.globalConsole
import newsref.db.initDb
import newsref.db.log.ConsoleConfig
import newsref.db.utils.cacheResource
import newsref.krawly.agents.*
import newsref.krawly.utils.pwFetch
import newsref.krawly.utils.pwFetchRedirect
import newsref.model.core.toUrl

fun main(args: Array<String>) {
	println(args.joinToString(", "))
	if (args.any {it.contains("http")}) {
		// test(args.first())
		testHead(args.first())
	} else {
		globalConsole.config = ConsoleConfig(showStatus = args.contains("showStatus"))
		crawl(args)
	}
}

fun crawl(args: Array<String>) {
	initDb()
	val web = SpiderWeb()
	val hostAgent = HostAgent(web)
	val leadMaker = LeadMaker(hostAgent)
	val feedChecker = FeedChecker(web, hostAgent, leadMaker)
	val leadFollower = LeadFollower(web = web, leadMaker = leadMaker, hostAgent = hostAgent)
	globalConsole.addCommand("start") {
		feedChecker.start()
		leadFollower.start()
		"Starting spider 🕷"
	}
	args.map { it.split('=') }
		.mapNotNull { if (it.size == 2 && it[0] == "cmd") it[1] else null }
		.forEach { globalConsole.sendCommand(it, null) }

	// Set the terminal to raw mode with no echo
	Runtime.getRuntime().exec(arrayOf("sh", "-c", "stty -icanon -echo min 1 time 0 < /dev/tty")).waitFor()
	Runtime.getRuntime().addShutdownHook(Thread {
		// Code to run on shutdown
		// closePlaywright()
		println("returning terminal settings")
		Runtime.getRuntime().exec(arrayOf("sh", "-c", "stty sane erase ^H < /dev/tty")).waitFor()
	})

	while (globalConsole.isActive) {
		val char = System.`in`.read()
		globalConsole.addInput(char.toChar())
	}
}

fun test(href: String) {
	val url = href.toUrl()
	val result = pwFetch(url, true)
//	result?.screenshot?.cacheResource(url, "png", "test/${url.domain}")
//	result?.requestHeaders?.map { "${it.key}:\n${it.value}" }?.joinToString("\n\n")
//		?.cacheResource(url.domain, "txt", "test/${url.domain}", "headers")
}

fun testHead(href: String) {
	val url = href.toUrl()
	val result = pwFetchRedirect(url)
	println(result.status)
	// result?.doc?.html?.cacheResource(url, "html", "test/head", url.host)
}
