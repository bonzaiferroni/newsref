package newsref.krawly.agents

import kotlinx.coroutines.*
import newsref.db.*
import newsref.db.model.Huddle
import newsref.db.services.*
import newsref.krawly.clients.GeminiClient
import newsref.model.core.ArticleType
import newsref.model.core.HuddleType
import newsref.model.core.HuddleType.*
import kotlin.time.Duration.Companion.hours

private val console = globalConsole.getHandle("HuddleRunner")

class HuddleCompleter(
    private val client: GeminiClient,
    private val service: HuddleRunnerService = HuddleRunnerService(),
    private val articleService: NewsArticleService = NewsArticleService(),
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

            data class ResponseGroup(val index: Int, val count: Int)
            val responseCounts = responses.groupingBy { it.response }
                .eachCount()
                .entries
                .map { ResponseGroup(it.key, it.value) }
                .sortedByDescending { it.count }

            val consensus = responseCounts[0]
            val runnerUp = responseCounts.getOrNull(1)
            if (runnerUp != null && consensus.index == runnerUp.index) {
                service.updateFinishedAt(huddle.id, huddle.finishedAt + 1.hours)
                console.logInfo("Tie vote for huddleId: ${huddle.id}")
                continue
            }

            val (label, stringValue) = huddle.options[consensus.index]

            if (huddle.huddleType != EditArticleType) continue

            when (huddle.huddleType) {
                ChapterSourceRelevance -> TODO()
                CreateChapter -> TODO()
                EditArticleType -> completeArticleType(huddle, stringValue)
            }

            service.completeHuddle(huddle.id, consensus.index)
        }
    }

    private suspend fun completeArticleType(huddle: Huddle, stringValue: String) {
        val value = ArticleType.valueOf(stringValue)
        articleService.updateArticleType(huddle.pageId, huddle.id, value)
    }
}