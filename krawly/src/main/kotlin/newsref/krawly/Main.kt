package newsref.krawly

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import newsref.db.globalConsole
import newsref.db.initDb
import newsref.db.log.ConsoleConfig
import newsref.db.utils.cacheResource
import newsref.db.utils.cacheSerializable
import newsref.krawly.agents.*
import newsref.krawly.utils.pwFetch
import newsref.model.core.Url
import newsref.model.core.toUrl
import org.jline.terminal.TerminalBuilder
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
	println(args.joinToString(", "))
	if (args.any {it.contains("http")}) {
		test(args.first())
	} else {
		globalConsole.config = ConsoleConfig(showStatus = args.contains("showStatus"))
		crawl()
	}
}

fun crawl() {
	initDb()
	val web = SpiderWeb()
	val outletAgent = OutletAgent(web)
	val leadMaker = LeadMaker(outletAgent)
	val feedChecker = FeedChecker(web, outletAgent, leadMaker)
	val leadFollower = LeadFollower(web, leadMaker, outletAgent)
	feedChecker.start()
	leadFollower.start()

	// Set the terminal to raw mode with no echo
	Runtime.getRuntime().exec(arrayOf("sh", "-c", "stty -icanon -echo min 1 time 0 < /dev/tty")).waitFor()
	Runtime.getRuntime().addShutdownHook(Thread {
		// Code to run on shutdown
		Runtime.getRuntime().exec(arrayOf("sh", "-c", "stty sane erase ^H < /dev/tty")).waitFor()
	})

	// Capture input in a coroutine
	while (globalConsole.isActive) {
		val char = System.`in`.read()
		globalConsole.addInput(char.toChar())
	}
}

fun test(href: String) {
	val url = href.toUrl()
	val result = pwFetch(url, true)
	result?.screenshot?.cacheResource(url, "png", "test/${url.host}")
	result?.requestHeaders?.map { "${it.key}:\n${it.value}" }?.joinToString("\n\n")
		?.cacheResource(url, "txt", "test/${url.host}", "headers")
}
