package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.services.NexusService
import kotlin.time.Duration.Companion.hours
import kotlin.time.measureTime

class NexusFinder(
	private val nexusService: NexusService = NexusService(),
) {
	private val console = globalConsole.getHandle("NexusFinder")

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			try {
				while (true) {
					console.log("finding nexuses")
					findNexuses()
					console.log("finished nexus quest")
					delay(1.hours)
				}
			} catch (e: Exception) {
				console.logError("NexusFinder error: ${e.message}\n${e.stackTraceToString()}")
			}
		}
	}

	private suspend fun findNexuses() {
		val cores = printTime("getHostLinkMap", console::log) {
			nexusService.getHostCores()
		}
		for (core in cores) {
			if (core.split('.').size < 2) {
				console.log("invalid domain: $core")
				continue
			}
			for (other in cores) {
				if (core == other) continue
				if (!other.endsWith(".${core}")) continue
				val nexus = nexusService.createNexus(core, other)
				console.log("Nexus created: ${nexus?.name}")
			}
		}
	}
}

const val NEXUS_THRESHOLD = .25
const val NEXUS_SUM_THRESHOLD = 100

suspend fun <T> printTime(name: String, console: (String) -> Unit = { println(it) }, block: suspend () -> T): T {
	var value: T
	val millis = measureTime { value = block() }
	console("$name took $millis ms")
	return value
}