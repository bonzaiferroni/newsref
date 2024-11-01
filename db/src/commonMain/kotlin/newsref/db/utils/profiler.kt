package newsref.db.utils

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import java.io.File
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTime

private var logger: (Any) -> Unit = ::println

fun initProfiler(func: (Any) -> Unit) {
	logger = func
}

suspend fun <T> profile(name: String, consoleLog: Boolean = false, block: suspend () -> T): T {
	var value: T
	val duration = measureTime { value = block() }
	recordTime(name, duration)
	if (consoleLog)
		logger("$name: ${duration.toDouble(DurationUnit.SECONDS).format(1)}")
	return value
}

private val files = mutableMapOf<String, File>()

private fun recordTime(name: String, duration: Duration) {
	val file = files.getOrPut(name) {
		val file = File("$RESOURCE_PATH/profiler/$name.log")
		if (!file.exists()) {
			file.parentFile?.mkdir()
		}
		reportStats(name, file)
		file.appendText("#${Clock.System.now()}\n")
		file
	}
	file.appendText("${duration.toDouble(DurationUnit.SECONDS).format(1)}\n")
}

private fun reportStats(name: String, file: File) {
	if (!file.exists()) return
	val text = file.readText()
	val samples = text.split('#').filter { it.isNotEmpty() }.map { sampleString ->
		val split = sampleString.split('\n')
		val sampleName = split.first()
		val values = split.mapNotNull { it.toDoubleOrNull() }
		val average = values.average()
		Triple(sampleName, average, values.size)
	}
	var totalAverage = 0.0
	var totalSize = 0
	logger("$name results:")
	for ((sampleName, average, sampleSize) in samples) {
		val delta = average - totalAverage
		logger("$sampleName average: ${average.format()} delta: ${delta.format(1, true)}")
		totalAverage = (totalAverage * totalSize + average * sampleSize) / (totalSize + sampleSize)
		totalSize += sampleSize
	}
}