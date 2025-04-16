package newsref.krawly

import klutch.environment.*
import newsref.db.globalConsole
import newsref.db.initDb
import newsref.db.console.ConsoleConfig
import newsref.db.utils.initProfiler
import newsref.krawly.agents.*
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.LocationClient

fun main(args: Array<String>) {
    println(args.joinToString(", "))
    globalConsole.config = ConsoleConfig(showStatus = args.contains("showStatus"))
    crawl(args)
}

fun crawl(args: Array<String>) {
    initProfiler(globalConsole.getHandle("profiler")::log)
    val env = readEnvFromPath()
    initDb(env)
    val web = SpiderWeb()
    val aiClient = GeminiClient(
        env.read("GEMINI_KEY_RATE_LIMIT_A"),
        env.read("GEMINI_KEY_RATE_LIMIT_B")
    )
    val locationClient = LocationClient(env.read("GOOGLE_MAPS_KEY"))
    val pageScoreFinder = PageScoreFinder()
    val hostScoreFinder = HostScoreFinder()
    val hostAgent = HostAgent(web)
    val leadMaker = LeadMaker()
    val articleReader = ArticleReader(aiClient, locationClient)
    val chapterComposer = ChapterComposer(aiClient, articleReader)
    val chapterReporter = ChapterReporter()
    val chapterPromoter = ChapterPromoter(aiClient)
    val chapterLinker = ChapterLinker()
    val storyComposer = StoryComposer()
    // val storyWatcher = StoryWatcher(env)
    // val noteWriter = NoteWriter()
    // val distanceFinder = DistanceFinder(env)
    val feedChecker = FeedChecker(web, hostAgent, leadMaker)
    val leadFollower = LeadFollower(web = web, leadMaker = leadMaker, hostAgent = hostAgent)
    val huddleCompleter = HuddleCompleter(aiClient)
    globalConsole.addCommand("start") {
        feedChecker.start()
        leadFollower.start()
        pageScoreFinder.start()
        hostScoreFinder.start()
        chapterComposer.start()
        chapterLinker.start()
        chapterPromoter.start()
        chapterReporter.start()
        storyComposer.start()
        // articleReader.start()
        // storyWatcher.start()
        // noteWriter.start()
        // distanceFinder.start()
        huddleCompleter.start()
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