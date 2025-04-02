package newsref.krawly.agents

import kotlinx.coroutines.*
import newsref.db.*
import newsref.db.services.*
import newsref.krawly.clients.GeminiClient
import kotlin.time.Duration.Companion.hours

private val console = globalConsole.getHandle("HuddleRunner")

class HuddleCompleter(
    private val client: GeminiClient,
    private val service: HuddleCompleterService = HuddleCompleterService(),
    private val articleService: ArticleService = ArticleService(),
) {
    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            console.logTrace("running huddles")
            while (true) {
                completeHuddles()
                delay(100)
            }
        }
    }

//    private suspend fun checkHuddles() {
//        val activeHuddles = service.readActiveHuddles()
//    }

    private suspend fun completeHuddles() {
        val completedHuddles = service.readCompletedHuddles()
        for (huddle in completedHuddles) {
            val responses = service.readResponses(huddle.id).groupBy { it.userId }
                .mapValues { (_, responses) -> responses.maxBy { it.time } }
                .values
            if (responses.isEmpty()) {
                service.deleteHuddle(huddle.id)
                console.logError("Found a huddle with no responses, deleting")
                continue
            }

            data class ResponseGroup(val value: String, val count: Int)
            val responseCounts = responses.groupingBy { it.response }
                .eachCount()
                .entries
                .map { ResponseGroup(it.key, it.value) }
                .sortedByDescending { it.count }

            val consensus = responseCounts[0]
            val runnerUp = responseCounts.getOrNull(1)
            if (runnerUp != null && consensus.count == runnerUp.count) {
                service.updateFinishedAt(huddle.id, huddle.finishedAt + 1.hours)
                console.logInfo("Tie vote for huddleId: ${huddle.id}")
                continue
            }

            service.completeHuddle(consensus.value, huddle)
            console.log("Completed huddle #${huddle.id}, result: ${consensus.value}")
        }
    }
}