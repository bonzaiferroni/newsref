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