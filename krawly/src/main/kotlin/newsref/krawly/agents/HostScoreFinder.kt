package newsref.krawly.agents

import kotlinx.coroutines.*
import newsref.db.*
import newsref.db.services.*
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("HostScoreFinder")

class HostScoreFinder(
    val hostScoreService: HostScoreService = HostScoreService()
) {

    fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                // delay(1.minutes)
                console.logTrace("finding host scores", "🕷 ")
                try {
                    findScores()
                } catch (e: Exception) {
                    console.logError("error finding scores\n${e}\n${e.stackTrace}")
                }
                console.logTrace("sleeping", "zz")
                delay(14.minutes)
            }
        }
    }

    private suspend fun findScores() {
        val signals = hostScoreService.findScoreSignals()
        hostScoreService.updateScores(signals)
        console.log("${signals.size} Host scores:")
        for (signal in signals.take(10)) {
            console.log("${signal.score.toString().padStart(5)} -> ${signal.core}")
        }
    }
}