package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.*
import newsref.db.services.*
import newsref.krawly.clients.AiClient
import newsref.model.data.*
import java.io.File
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("ChapterFinder")

class ChapterFinder(
    private val vectorClient: VectorClient = VectorClient(),
    private val vectorService: VectorService = VectorService(),
    private val chapterService: ChapterService = ChapterService(),
    private val contentService: ContentService = ContentService(),
    private val narratorService: NarratorService = NarratorService(),
) {

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            console.logTrace("looking for sources")
            while (true) {
                findNewChapter()
                delay(1.minutes)
            }
        }
    }

    private suspend fun findNewChapter() {
        val modelId = vectorService.readOrCreateModel(defaultModelName)

        val origin = chapterService.findOrigin()
        if (origin == null) {
            console.log("missing origin")
            return
        }

        val concurrentSources = chapterService.readConcurrentSources(origin)

        val inboundSources = chapterService.findInboundSources(origin.id)
        if (inboundSources.isEmpty()) {
            console.log("no inbounds")
            return
        }

        for (source in inboundSources) {
            if (vectorService.readVector(source.id, modelId) != null) continue
            val content = contentService.readSourceContentText(source.id)
            if (content.length < VECTOR_MIN_WORDS) throw error("content too small: ${content.length}")
            val vector = vectorClient.fetchVector(origin, defaultModelName, content) ?: error("unable to fetch vector")
            vectorService.insertVector(source.id, defaultModelName, vector)
        }

        val inboundSourceIds = inboundSources.map { it.id }
        val distances = vectorService.readDistances(inboundSourceIds, modelId)
        val centralSource = inboundSources.minBy { source ->
            val sourceDistances = distances.getValue(source.id)
            sourceDistances.sumOf { it.distance.toDouble() } / sourceDistances.size
        }
        // val primarySources = chapterService.findPrimarySources(inboundSourceIds)
    }
}

private const val defaultModelName = "text-embedding-3-small"


