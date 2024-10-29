package newsref.krawly.utils

import newsref.db.globalConsole
import kotlin.time.DurationUnit
import kotlin.time.measureTime

private val console = globalConsole.getHandle("profiler")

suspend fun <T> profile(name: String, block: suspend () -> T): T {
	var value: T
	val duration = measureTime { value = block() }
	console.log("$name: ${duration.toString(DurationUnit.SECONDS)}")
	return value
}