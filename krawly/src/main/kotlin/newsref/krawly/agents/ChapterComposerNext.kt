@file:Suppress("DuplicatedCode")

package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.Environment
import newsref.db.core.VectorModel
import newsref.db.globalConsole
import newsref.db.model.ChapterFinderLog
import newsref.db.model.ChapterFinderState
import newsref.db.model.Source
import newsref.db.services.CHAPTER_MAX_DISTANCE
import newsref.db.services.ChapterComposerNextService
import newsref.db.services.ChapterSourceSignal
import newsref.db.services.ContentService
import newsref.db.services.DataLogService
import newsref.db.services.EMBEDDING_MAX_CHARACTERS
import newsref.db.services.EMBEDDING_MIN_CHARACTERS
import newsref.db.services.EMBEDDING_MIN_WORDS
import newsref.db.services.SourceVectorService
import newsref.db.tables.ChapterSourceTable.chapterId
import newsref.model.core.SourceType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("ChapterComposer")

class ChapterComposerNext(
    env: Environment,
    private val vectorClient: VectorClient = VectorClient(env),
    private val sourceVectorService: SourceVectorService = SourceVectorService(),
    private val service: ChapterComposerNextService = ChapterComposerNextService(),
    private val contentService: ContentService = ContentService(),
    private val dataLogService: DataLogService = DataLogService(),
) : StateModule<ChapterFinderState>(ChapterFinderState()) {
    private val excludedIds = mutableListOf<Pair<Long, Instant>>()

    fun start() {
        coroutineScope.launch {
            val savedState = dataLogService.read(ChapterFinderLog.state)
            if (savedState != null)
                setState { savedState }
            while (true) {
                delay(1.minutes)
                dataLogService.write(ChapterFinderLog.state, stateNow)
            }
        }
        coroutineScope.launch {
            val model = sourceVectorService.readOrCreateModel(defaultModelName)
            while (true) {
                readNextSignal(model)
            }
        }
    }

    private fun excludeUntil(sourceId: Long, duration: Duration) =
        excludedIds.add(sourceId to Clock.System.now() + duration)

    private suspend fun readNextSignal(model: VectorModel) {
        val now = Clock.System.now()
        val excludedIds = this.excludedIds.filter { it.second > now }.map { it.first }
        val origin = service.findNextSignal(excludedIds)
        if (origin == null) {
            setState { it.copy(emptySignals = it.emptySignals + 1) }
            delay(1.minutes)
            return
        }

        if (origin.source.type == SourceType.ARTICLE) {
            setState { it.copy(secondarySignals = it.secondarySignals + 1) }
            findSecondaryBucket(origin, model)
        } else {
            setState { it.copy(primarySignals = it.primarySignals + 1) }
            // findPrimaryBucket(origin, model)
        }
    }

    private suspend fun findSecondaryBucket(signal: ChapterSourceSignal, model: VectorModel) {
        val vector = readOrFetchVector(signal.source, model)
        if (vector == null) {
            excludeUntil(signal.source.id, Duration.INFINITE)
            // console.log("vector unavailable")
            return
        }

        val chapterSignals = service.findTextRelatedChapters(signal.source.id, vector)
        val buckets = chapterSignals.mapNotNull { (chapter, textDistance) ->
            val timeDistance = signal.source.existedAt.chapterDistanceTo(chapter.happenedAt)
            val bucketDistance = BucketDistance(textDistance, timeDistance, 0f, 0f)
            if (bucketDistance.magnitude > CHAPTER_MAX_DISTANCE) return@mapNotNull null
            val bucket = ChapterBucket(chapter)
            val sourceSignals = service.readChapterSourceSignals(chapter.id)
            for (signal in sourceSignals) {
                val vector = readOrFetchVector(signal.source, model) ?: error("unable to find expected vector")
                bucket.add(signal, vector)
            }
            bucket
        }
        if (buckets.isEmpty()) {
            // create chapter
            return
        }
        val sampleMaxSize = buckets.maxBy { it.size }.size
        val bucket = buckets.minBy { it.getDistanceVector(signal, vector, sampleMaxSize).priorityMagnitude }
        bucket.add(signal, vector)
        val removedIds = bucket.shake()
        if (removedIds.isNotEmpty()) {
            console.log("${removedIds.size} shaken out of bucket")
        }

        val sources = bucket.getPrimarySources() + bucket.getSecondarySources()

        console.log("updated chapter! size: ${bucket.size}")
        service.updateChapterAndSources(
            chapterId = bucket.chapterId,
            score = bucket.getChapterScore(),
            size = bucket.size,
            cohesion = bucket.cohesion,
            happenedAt = bucket.happenedAt,
            sources = sources,
            vector = bucket.averageVector
        )
        setState { it.copy(chaptersUpdated = it.chaptersUpdated + 1) }
    }

    private suspend fun readOrFetchVector(source: Source, model: VectorModel): FloatArray? {
        val cachedVector = sourceVectorService.readVector(source.id, model.id)
        if (cachedVector != null) {
            return cachedVector
        }
        val wordCount = source.contentCount
        val content = contentService.readSourceContentText(source.id).take(EMBEDDING_MAX_CHARACTERS)
        if (content.length < EMBEDDING_MIN_CHARACTERS || wordCount == null || wordCount < EMBEDDING_MIN_WORDS) {
            setState { it.copy(contentsMissing = it.contentsMissing + 1) }
            return null
        }
        // console.log("fetching vector")
        setState { it.copy(vectorsFetched = it.vectorsFetched + 1) }
        val vector = vectorClient.fetchVector(source, model.name, content) ?: error("unable to fetch vector")
        sourceVectorService.insertVector(source.id, model.name, vector)
        return vector
    }
}