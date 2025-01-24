package newsref.krawly

import newsref.db.globalConsole
import newsref.db.initDb
import newsref.db.log.ConsoleConfig
import newsref.db.utils.initProfiler
import newsref.krawly.agents.*

fun main(args: Array<String>) {
	println(args.joinToString(", "))
	globalConsole.config = ConsoleConfig(showStatus = args.contains("showStatus"))
	crawl(args)
}

fun crawl(args: Array<String>) {
	initProfiler(globalConsole.getHandle("profiler")::log)
	initDb()
	val web = SpiderWeb()
	val scoreFinder = ScoreFinder()
	val hostAgent = HostAgent(web)
	val leadMaker = LeadMaker()
	val noteWriter = NoteWriter()
	val distanceFinder = DistanceFinder()
	val feedChecker = FeedChecker(web, hostAgent, leadMaker)
	val leadFollower = LeadFollower(web = web, leadMaker = leadMaker, hostAgent = hostAgent)
	globalConsole.addCommand("start") {
		feedChecker.start()
		leadFollower.start()
		scoreFinder.start()
		// noteWriter.start()
		distanceFinder.start()
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