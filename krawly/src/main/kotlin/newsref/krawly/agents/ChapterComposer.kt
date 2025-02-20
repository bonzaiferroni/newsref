@file:Suppress("DuplicatedCode")

package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.Environment
import newsref.db.core.VectorModel
import newsref.db.globalConsole
import newsref.db.model.Chapter
import newsref.db.model.ChapterFinderLog
import newsref.db.model.ChapterFinderState
import newsref.db.model.Source
import newsref.db.services.CHAPTER_MAX_DISTANCE
import newsref.db.services.CHAPTER_MERGE_FACTOR
import newsref.db.services.ChapterComposerService
import newsref.db.services.ChapterSourceSignal
import newsref.db.services.ContentService
import newsref.db.services.DataLogService
import newsref.db.services.EMBEDDING_MAX_CHARACTERS
import newsref.db.services.EMBEDDING_MIN_CHARACTERS
import newsref.db.services.EMBEDDING_MIN_WORDS
import newsref.db.services.SourceVectorService
import newsref.model.core.SourceType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("ChapterComposer")

class ChapterComposer(
    env: Environment,
    private val vectorClient: VectorClient = VectorClient(env),
    private val sourceVectorService: SourceVectorService = SourceVectorService(),
    private val service: ChapterComposerService = ChapterComposerService(),
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
        setState { it.copy(exclusions = excludedIds.size) }

        val origin = service.findNextSignal(excludedIds)
        if (origin == null) {
            setState { it.copy(emptySignals = it.emptySignals + 1) }
            delay(1.minutes)
            return
        }

        setState { it.copy(signalDate = origin.source.seenAt) }
        if (origin.source.type == SourceType.ARTICLE) {
            // console.log("found secondary signal")
            setState { it.copy(secondarySignals = it.secondarySignals + 1) }
            findSecondaryBucket(origin, model)
        } else {
            // console.log("found primary signal")
            setState { it.copy(primarySignals = it.primarySignals + 1) }
            findPrimaryBucket(origin, model)
        }
    }

    private suspend fun findPrimaryBucket(origin: ChapterSourceSignal, model: VectorModel) {
        val signals = service.readInboundSignals(origin.source.id)
        if (signals.isEmpty()) {
            excludeUntil(origin.source.id, 1.hours)
            console.log("primary bucket too small")
            return
        }
        val bucket = ChapterBucket()
        for (signal in signals) {
            if (signal.source.type != SourceType.ARTICLE) continue
            if (bucket.contains(signal.source.id)) continue
            val vector = readOrFetchVector(signal.source, model) ?: continue
            bucket.add(signal, vector)
        }
        bucket.shake()
        if (bucket.size == 0) {
            console.log("primary bucket too small after shake")
            return
        }

        if (findRelatedBucketAndMerge(bucket, model)) {
            return
        }
        console.log("adding primary bucket")
        createChapter(bucket)
    }

    private suspend fun findSecondaryBucket(signal: ChapterSourceSignal, model: VectorModel) {
        val vector = readOrFetchVector(signal.source, model)
        if (vector == null) {
            excludeUntil(signal.source.id, Duration.INFINITE)
            console.log("vector unavailable")
            return
        }

        val buckets = findBuckets(listOf(signal), vector, signal.source.existedAt, CHAPTER_MAX_DISTANCE, model)
        if (buckets.isEmpty()) {
            // create chapter
            val bucket = ChapterBucket()
            bucket.add(signal, vector)
            createChapter(bucket)
            return
        }
        val sampleMaxSize = buckets.maxBy { it.size }.size
        val bucket = buckets.minBy { it.getDistanceVector(signal, vector, sampleMaxSize).priorityMagnitude }
        bucket.add(signal, vector)
        bucket.shake()
        if (findRelatedBucketAndMerge(bucket, model)) {
            return
        }
        updateChapter(bucket)
    }

    private suspend fun findRelatedBucketAndMerge(bucket: ChapterBucket, model: VectorModel): Boolean {
        val buckets = findBuckets(
            originSignals = bucket.signals,
            vector = bucket.averageVector,
            happenedAt = bucket.happenedAt,
            maxDistance = CHAPTER_MAX_DISTANCE * CHAPTER_MERGE_FACTOR,
            model = model
        )
        if (buckets.isEmpty()) {
            return false
        }
        val sampleMaxSize = buckets.maxBy { it.size }.size
        val existingBucket = buckets.minBy { it.getDistanceVector(bucket, sampleMaxSize).priorityMagnitude }
        val (smaller, bigger) = when {
            existingBucket.size < bucket.size && bucket.chapterId != null -> existingBucket to bucket
            else -> bucket to existingBucket
        }
        smaller.mergeInto(bigger)
        bigger.shake()
        console.log("merged bucket, new size: ${bigger.size}")
        updateChapter(bigger)
        deleteChapter(smaller)
        return true
    }

    private suspend fun createChapter(bucket: ChapterBucket) {
        console.log("created chapter!")
        val sources = bucket.getSecondarySources() + bucket.getPrimarySources()
        service.createChapter(
            chapter = Chapter(
                score = bucket.getChapterScore(),
                size = 1,
                cohesion = 1f,
                createdAt = Clock.System.now(),
                happenedAt = bucket.happenedAt,
                storyDistance = null
            ),
            sources = sources,
            vector = bucket.averageVector
        )
        setState { it.copy(chaptersCreated = it.chaptersCreated + 1) }
    }

    private suspend fun updateChapter(bucket: ChapterBucket) {
        val sources = bucket.getPrimarySources() + bucket.getSecondarySources()

        console.log("updated chapter! size: ${bucket.size}")
        service.updateChapterAndSources(
            chapterId = bucket.chapterId!!,
            score = bucket.getChapterScore(),
            size = bucket.size,
            cohesion = bucket.cohesion,
            happenedAt = bucket.happenedAt,
            sources = sources,
            vector = bucket.averageVector
        )
        setState { it.copy(chaptersUpdated = it.chaptersUpdated + 1) }
    }

    private suspend fun deleteChapter(bucket: ChapterBucket): Boolean {
        val chapterId = bucket.chapterId ?: return false
        console.log("deleted chapter")
        service.deleteChapter(chapterId)
        setState { it.copy(chaptersDeleted = it.chaptersDeleted + 1) }
        return true
    }

    private suspend fun findBuckets(
        originSignals: List<ChapterSourceSignal>,
        vector: FloatArray,
        happenedAt: Instant,
        maxDistance: Float,
        model: VectorModel
    ): List<ChapterBucket> {
        val sourceIds = originSignals.map { it.source.id }
        val chapterSignals = service.findTextRelatedChapters(sourceIds, vector)
        return chapterSignals.mapNotNull { (chapter, textDistance) ->
            val timeDistance = happenedAt.chapterDistanceTo(chapter.happenedAt)
            val bucketDistance = BucketDistance(textDistance, timeDistance, 0f, 0f)
            if (bucketDistance.magnitude > maxDistance) return@mapNotNull null
            val bucket = ChapterBucket(chapter)
            val sourceSignals = service.readChapterSourceSignals(chapter.id)
            for (signal in sourceSignals) {
                val vector = readOrFetchVector(signal.source, model) ?: error("unable to find expected vector")
                bucket.add(signal, vector)
            }
            bucket
        }
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