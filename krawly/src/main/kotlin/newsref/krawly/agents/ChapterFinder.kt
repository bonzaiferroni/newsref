package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.*
import newsref.db.services.*
import newsref.db.utils.*
import newsref.model.data.*
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

        val inboundSources = chapterService.readInboundSources(origin.id)
        if (inboundSources.isEmpty()) {
            console.log("no inbounds")
            return
        }

        val vectors = mutableMapOf<Long, FloatArray>()
        for (source in inboundSources) {
            val cachedVector = vectorService.readVector(source.id, modelId)
            if (cachedVector != null) {
                vectors[source.id] = cachedVector
                continue
            }
            val content = contentService.readSourceContentText(source.id)
            if (content.length < VECTOR_MIN_WORDS) throw error("content too small: ${content.length}")
            val vector = vectorClient.fetchVector(origin, defaultModelName, content) ?: error("unable to fetch vector")
            vectorService.insertVector(source.id, defaultModelName, vector)
            vectors[source.id] = vector
        }

        fun Float.format() = String.format("%.2f", this)
        val average = averageAndNormalize(vectors.values.toList())
        for((id, vector) in vectors) {
            val cosineSimilarity = cosineSimilarity(average, vector)
            val dotProduct = dotProduct(average, vector)
            console.log("$id: ${cosineSimilarity.format()} / ${dotProduct.format()}")
        }


        // val concurrentSources = chapterService.readConcurrentSources(origin)
        // val primarySources = chapterService.findPrimarySources(inboundSourceIds)
    }
}

private const val defaultModelName = "text-embedding-3-small"


