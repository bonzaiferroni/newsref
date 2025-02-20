package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.datetime.*
import newsref.db.*
import newsref.db.core.*
import newsref.db.model.Chapter
import newsref.db.model.ChapterFinderLog
import newsref.db.model.ChapterFinderState
import newsref.db.model.Relevance
import newsref.db.model.Source
import newsref.db.services.*
import newsref.model.core.*
import kotlin.time.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("ChapterComposer")

class ChapterComposerPrevious(
    env: Environment,
    private val vectorClient: VectorClient = VectorClient(env),
    private val sourceVectorService: SourceVectorService = SourceVectorService(),
    private val service: ChapterComposerServicePrevious = ChapterComposerServicePrevious(),
    private val contentService: ContentService = ContentService(),
    private val dataLogService: DataLogService = DataLogService(),
) : StateModule<ChapterFinderState>(ChapterFinderState()) {
    private val excludedIds = mutableListOf<Pair<Long, Instant>>()
    private val buckets = mutableListOf<ChapterBucket>()

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
            initCurrentChapters(model)
            while (true) {
                readNextSignal(model)
            }
        }
    }

    private fun excludeUntil(sourceId: Long, duration: Duration) =
        excludedIds.add(sourceId to Clock.System.now() + duration)

    private suspend fun initCurrentChapters(model: VectorModel) {
        val chapters = service.readCurrentChapters(4)
        chapters.forEach {
            val bucket = ChapterBucket(it)
            val signals = service.readChapterSignals(it.id)
            if (signals.isEmpty()) {
                console.logError("deleted chapter of size ${it.size} with no signals\n${it.title}")
                service.deleteChapter(it.id)
                return@forEach
            }
            for (signal in signals) {
                val vector = readOrFetchVector(signal.source, model) ?: error("unable to find expected vector")
                bucket.add(signal, vector)
            }
            updateRelevance(bucket)
            buckets.add(bucket)
        }
    }

    private suspend fun readNextSignal(model: VectorModel) {
        val now = Clock.System.now()
        val excludedIds = this.excludedIds.filter { it.second > now }.map { it.first }.toMutableList()
        for (bucket in buckets) {
            if (bucket.chapterId != null) continue
            excludedIds.addAll(bucket.signals.map { it.source.id })
            excludedIds.addAll(bucket.linkIds)
        }

        setState {
            it.copy(
                exclusions = excludedIds.size,
                buckets = buckets.size,
                chapters = buckets.filter { it.chapterId != null }.size
            )
        }

        val origin = service.findNextSignal(excludedIds)
        if (origin == null) {
            setState { it.copy(emptySignals = it.emptySignals + 1) }
            delay(1.minutes)
            return
        }
        setState { it.copy(signalDate = origin.source.seenAt) }
        // console.log("origin: ${origin.source.id} -> ${origin.source.type}")
        if (origin.source.type == SourceType.ARTICLE) {
            setState { it.copy(secondarySignals = it.secondarySignals + 1) }
            findSecondaryBucket(origin, model)
        } else {
            setState { it.copy(primarySignals = it.primarySignals + 1) }
            findPrimaryBucket(origin, model)
        }
    }

    private suspend fun findPrimaryBucket(origin: ChapterSourceSignal, model: VectorModel) {
        excludeUntil(origin.source.id, 1.hours)
        val signals = service.readInboundSignals(origin.source.id)
        if (signals.isEmpty()) {
            // console.log("primary bucket too small")
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
            // console.log("primary bucket too small")
            return
        }

        val result = buckets.map {
            val distance = it.getDistanceVector(bucket).magnitude
            Pair(distance, it)
        }.minByOrNull { it.first }
        if (result == null || result.first > CHAPTER_MAX_DISTANCE * CHAPTER_MERGE_FACTOR) {
            // console.log("adding primary bucket")
            buckets.add(bucket)
            checkBucket(bucket)
        } else {
            val (_, existingBucket) = result
            val initialSize = existingBucket.size
            bucket.mergeInto(existingBucket)
            if (existingBucket.size > initialSize) {
                // console.log("merged primary bucket: ${existingBucket.chapterId}")
                checkBucket(bucket)
            } else {
                // console.log("merge primary bucket didn't grow: ${existingBucket.chapterId}")
            }
        }
    }

    private suspend fun findSecondaryBucket(signal: ChapterSourceSignal, model: VectorModel) {
        val vector = readOrFetchVector(signal.source, model)
        if (vector == null) {
            excludeUntil(signal.source.id, Duration.INFINITE)
            // console.log("vector unavailable")
            return
        }

        val result = buckets // .filter { !it.irrelevantIds.contains(signal.source.id) }
            .map {
                val distance = it.getDistanceVector(signal, vector).magnitude
                Pair(distance, it)
            }.minByOrNull { it.first }
        if (result == null || result.first > CHAPTER_MAX_DISTANCE) {
            // console.log("adding new bucket")
            val bucket = ChapterBucket()
            bucket.add(signal, vector)
            buckets.add(bucket)
        } else {
            // console.log("adding ${signal.source.id} to bucket")
            val (_, bucket) = result
            if (bucket.contains(signal.source.id)) {
                console.logError(
                    "signal: ${signal.source.id} already in bucket: ${bucket.chapterId}\n" +
                            "source: ${signal.source.title}\n" +
                            "bucket: ${bucket.title}"
                )
                updateRelevance(bucket)
                excludeUntil(signal.source.id, 1.hours)
                return
            }
            bucket.add(signal, vector)
            val result = buckets.mapNotNull {
                    if (it == bucket) return@mapNotNull null
                    val distance = it.getDistanceVector(bucket).magnitude
                    Pair(distance, it)
                }.minByOrNull { it.first }
            if (result == null || result.first > CHAPTER_MAX_DISTANCE) {
                checkBucket(bucket)
            } else {
                val (_, existingBucket) = result
                val (smaller, bigger) = when {
                    existingBucket.size < bucket.size -> existingBucket to bucket
                    else -> bucket to existingBucket
                }
                smaller.mergeInto(bigger)
                console.log("merged buckets with sizes: ${smaller.size} to ${bigger.size}")
                checkBucket(bigger)
                val chapterId = smaller.chapterId
                if (chapterId != null) {
                    console.log("deleted merged bucket")
                    service.deleteChapter(chapterId)
                    buckets.remove(smaller)
                }
            }
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

    private suspend fun checkBucket(bucket: ChapterBucket) {
        updateRelevance(bucket)

        val removedIds = bucket.shake()
        val chapterId = bucket.chapterId

        if (bucket.size < CHAPTER_MIN_ARTICLES) {
            if (chapterId != null) {
                console.log("deleted chapter")
                service.deleteChapter(chapterId)
                buckets.remove(bucket)
                setState { it.copy(chaptersDeleted = it.chaptersDeleted + 1) }
            }
        } else {
            val sources = bucket.getPrimarySources() + bucket.getSecondarySources()
            if (chapterId != null) {
                if (removedIds.isNotEmpty()) {
                    console.log("${removedIds.size} shaken out of bucket")
                    // removedIds.forEach { chapterFinderService.deleteChapterSource(chapterId, it) }
                }

                console.log("updated chapter! size: ${bucket.size}")
                service.updateChapterAndSources(
                    chapterId = chapterId,
                    score = bucket.getChapterScore(),
                    size = sources.size,
                    cohesion = bucket.cohesion,
                    happenedAt = bucket.happenedAt,
                    sources = sources,
                    vector = bucket.averageVector
                )
                setState { it.copy(chaptersUpdated = it.chaptersUpdated + 1) }
            } else {
                console.log("created chapter!")
                val chapterId = service.createChapter(
                    chapter = Chapter(
                        score = bucket.getChapterScore(),
                        size = sources.size,
                        cohesion = bucket.cohesion,
                        createdAt = Clock.System.now(),
                        happenedAt = bucket.happenedAt,
                        storyDistance = null
                    ),
                    sources = sources,
                    vector = bucket.averageVector
                )
                bucket.setId(chapterId)
                setState { it.copy(chaptersCreated = it.chaptersCreated + 1) }
            }
        }
    }

    private suspend fun updateRelevance(bucket: ChapterBucket) {
        val chapterId = bucket.chapterId
        if (chapterId == null) return
        val signals = service.readCurrentRelevance(chapterId)
        val relevantIds = signals.filter { it.second == Relevance.Relevant }.map { it.first }.toSet()
        val irrelevantIds = signals.filter { it.second == Relevance.Irrelevant }.map { it.first }.toSet()
        // bucket.updateRelevance(relevantIds, irrelevantIds)
    }
}

const val defaultModelName = "text-embedding-3-small"

fun Instant.isWithinRange(instant: Instant, duration: Duration) = this > instant - duration && this < instant + duration
