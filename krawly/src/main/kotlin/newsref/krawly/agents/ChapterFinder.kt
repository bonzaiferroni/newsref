package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.*
import newsref.db.services.*
import newsref.db.utils.*
import newsref.model.data.Chapter
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("ChapterFinder")

class ChapterFinder(
    private val vectorClient: VectorClient = VectorClient(),
    private val vectorService: VectorService = VectorService(),
    private val chapterService: ChapterService = ChapterService(),
    private val contentService: ContentService = ContentService(),
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

        var secondarySources = chapterService.readInboundSources(origin.id).toMutableList()
        if (secondarySources.isEmpty()) {
            console.log("no inbounds")
            return
        }
        val happenedAt = secondarySources.minBy { (it.publishedAt ?: it.seenAt) }.let { it.publishedAt ?: it.seenAt }
        secondarySources.removeIf { (it.publishedAt ?: it.seenAt) - happenedAt < 3.days }
        console.log("found ${secondarySources.size} initial sources")

        suspend fun readOrFetchVector(sourceId: Long): FloatArray? {
            val cachedVector = vectorService.readVector(sourceId, modelId)
            if (cachedVector != null) { return cachedVector }
            console.log("fetching vector")
            val content = contentService.readSourceContentText(sourceId).take(VECTOR_MAX_CHARACTERS)
            if (content.length < VECTOR_MIN_WORDS) return null
            val vector = vectorClient.fetchVector(origin, defaultModelName, content) ?: error("unable to fetch vector")
            vectorService.insertVector(sourceId, defaultModelName, vector)
            return vector
        }

        val vectors = mutableMapOf<Long, FloatArray>()
        for (source in secondarySources.toList()) {
            val vector = readOrFetchVector(source.id)
            if (vector == null) {
                secondarySources.remove(source)
                continue
            }
            vectors[source.id] = vector
        }

        // iterative trim
        fun Float.format() = String.format("%.2f", this)
        var averageVector = averageAndNormalize(vectors.values.toList())
        val targetDistance = .2f
        var averageDistance = 1f
        while (averageDistance > targetDistance && vectors.size >= 3) {
            var sum = 0f
            var maxId = -1L
            var maxDistance = 0f
            for((id, vector) in vectors) {
                val distance = distance(averageVector, vector)
                sum += distance
                if (distance > maxDistance) {
                    maxDistance = distance
                    maxId = id
                }
            }
            averageDistance = sum / vectors.size
            if (averageDistance > targetDistance) {
                vectors.remove(maxId)
                val title = secondarySources.first { it.id == maxId }.title
                secondarySources.removeIf { it.id == maxId }
                console.log("Drop: ${maxDistance.format()} ${title?.take(56)}")
            }
            averageVector = averageAndNormalize(vectors.values.toList())
        }

        // trim above threshold
        val threshold = .4f
        for ((id, vector) in vectors.toMap()) {
            val distance = distance(averageVector, vector)
            if (distance > threshold) {
                val title = secondarySources.first { it.id == id }.title
                console.log("Drop: ${distance.format()} ${title?.take(56)}")
                vectors.remove(id)
                secondarySources.removeIf { it.id == id }
            }
        }

        // add concurrent sources that weren't inbounds of origin
        val concurrentSources = chapterService.readConcurrentTopSources(happenedAt)
        for (source in concurrentSources) {
            if (vectors[source.id] != null) continue
            val vector = readOrFetchVector(source.id) ?: continue
            val distance = distance(averageVector, vector)
            if (distance > targetDistance) continue
            console.log("concurrent: ${distance.format()} ${source.title}")
            vectors[source.id] = vector
            secondarySources.add(source)
        }

        if (vectors.any { (id, _) -> !secondarySources.any { it.id == id } }) error("vector not in secondarySources")
        if (secondarySources.any { vectors[it.id] == null }) error("secondarySource not in vectors")

        averageVector = averageAndNormalize(vectors.values.toList())

        val primarySources = chapterService.findPrimarySources(vectors.keys.toList())
        val chapterScore = secondarySources.sumOf { it.score ?: 0 } + primarySources.sumOf { it.score ?: 0 }
        console.log("chapterScore: $chapterScore")
        val chapter = Chapter(
            title = origin.title ?: "",
            createdAt = Clock.System.now(),
            happenedAt = happenedAt,
            score = chapterScore,
        )
        chapterService.addChapter(chapter, secondarySources, primarySources)
    }
}

private const val defaultModelName = "text-embedding-3-small"
