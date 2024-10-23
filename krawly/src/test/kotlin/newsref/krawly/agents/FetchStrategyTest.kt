package newsref.krawly.agents

import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import newsref.db.globalConsole
import newsref.db.log.forestNightBg
import newsref.db.log.toCyan
import newsref.db.utils.RESOURCE_PATH
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.pwFetch
import newsref.model.core.toUrl
import newsref.model.core.toUrlOrNull
import newsref.model.data.FetchStrategy
import java.io.File
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.measureTime

private var console = globalConsole.getHandle("test")

class FetchStrategyTest {
	@Test
	fun `explore code`() = coroutineScope {
		val file = File("$RESOURCE_PATH/test/urls.txt")
		val urls = file.readText().split('\n').mapNotNull { it.toUrlOrNull() }
			.groupBy { it.core }.map { it.value.first() }
		console.log("found ${urls.size} urls with unique hosts")

		val web = SpiderWeb()
		val pageReader = PageReader(HostAgent(web))
		val results = mutableMapOf<String, MutableList<FetchTest>>()
		for (strategy in FetchStrategy.entries) {
			var badStatusSum = 0
			var durationSum = Duration.ZERO
			var contentSum = 0
			var linkSum = 0
			for (url in urls) {
				val (result, duration) = trackTime { web.fetch(url, strategy) }
				durationSum += duration
				if (result.status != HttpStatusCode.OK.value) {
					badStatusSum++
					(results.getOrPut(url.href) { mutableListOf() }).add(FetchTest(strategy, true))
					val msg = result.exception?.split('\n')?.firstOrNull() ?: ""
					console.log("💢 ${url.core} $msg")
					continue
				}
				val page = pageReader.read(result, null, null)
				val contentLength = page?.contents?.sumOf { it.length } ?: 0
				val linkCount = page?.links?.size ?: 0
				contentSum += contentLength
				linkSum += linkCount
				console.cell(contentLength, 5, "words")
					.cell(linkCount, 5, "links")
					.cell(duration, 20, "duration")
					.row(url.core)
				(results.getOrPut(url.href) { mutableListOf() }).add(
					FetchTest(strategy, false, contentLength, linkCount, duration)
				)
			}
			console.cell(strategy, 10, "strategy")
				.cell(badStatusSum, 5, "bad")
				.cell(contentSum, 10, "words")
				.cell(linkSum, 5, "links")
				.cell(durationSum, 20, "duration")
				.row(background = forestNightBg)
		}
		val sb = StringBuilder()
		for ((url, list) in results) {
			sb.appendLine(url)
			for (item in list) {
				sb.cell(item.strategy, 10).cell(item.badRequest).cell(item.wordCount, 10).cell(item.links)
					.cell(item.duration,20 )
				sb.appendLine()
			}
			sb.appendLine()
		}
		val outFile = File("$RESOURCE_PATH/test/urls-out.txt")
		outFile.writeText(sb.toString())
	}

	@Test
	fun `explore ktor`() = coroutineScope {
		val pageReader = PageReader(HostAgent(SpiderWeb()))
		val url = "https://www.cnn.com/2024/10/15/media/trump-cancel-cnbc-interview-media/index.html".toUrl()
		val (result, duration) = trackTime { pwFetch(url) }
		if (result.status != HttpStatusCode.OK.value) {
			console.log("Bad Status (${result.status}): $url")
		}
		val page = pageReader.read(result, null, null)
		val contentLength = page?.contents?.sumOf { it.length } ?: 0
		val linkCount = page?.links?.size ?: 0
		console.log(url.href.toCyan())
		console.cell(contentLength, 5, "words")
			.cell(linkCount, 5, "links")
			.cell(duration, 20, "duration")
			.row()
	}
}

fun <T> trackTime(block: () -> T): Pair<T, Duration> {
	var value: T
	val millis = measureTime { value = block() }
	return Pair(value, millis)
}

fun coroutineScope(block: suspend () -> Unit) {
	runBlocking {
		block()
	}
}

data class FetchTest(
	val strategy: FetchStrategy,
	val badRequest: Boolean = false,
	val wordCount: Int = 0,
	val links: Int = 0,
	val duration: Duration = Duration.ZERO,
)

fun StringBuilder.cell(content: Any, width: Int = 5):StringBuilder {
	this.append("┃")
	this.append(content.toString().padStart(width).takeLast(width))
	return this
}