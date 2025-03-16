package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import newsref.db.*
import newsref.db.services.*
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate
import newsref.model.core.Relevance
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("HuddleRunner")

class HuddleRunner(
    private val client: GeminiClient,
    private val service: HuddleRunnerService = HuddleRunnerService()
) {
    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            console.logTrace("running huddles")
            while (true) {
                checkHuddles()
                completeHuddles()
                delay(100)
            }
        }
    }

    private suspend fun checkHuddles() {
        val activeHuddles = service.readActiveHuddles()
    }

    private suspend fun completeHuddles() {
        val completedHuddles = service.readCompletedHuddles()
        
    }
}