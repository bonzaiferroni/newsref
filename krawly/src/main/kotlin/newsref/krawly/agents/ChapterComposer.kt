@file:Suppress("DuplicatedCode")

package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.core.EmbeddingFamily
import newsref.db.globalConsole
import newsref.db.model.Chapter
import newsref.db.model.ChapterFinderLog
import newsref.db.model.ChapterFinderState
import newsref.db.model.Page
import newsref.db.services.CHAPTER_MAX_DISTANCE
import newsref.db.services.CHAPTER_MIN_ARTICLES
import newsref.db.services.ChapterComposerService
import newsref.db.services.ChapterPageSignal
import newsref.db.services.ContentService
import newsref.db.services.DataLogService
import newsref.db.services.EmbeddingService
import newsref.krawly.clients.GeminiClient
import newsref.model.data.ContentType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("ChapterComposer")

class ChapterComposer(
    aiClient: GeminiClient,
    private val articleReader: ArticleReader,
    private val embeddingClient: EmbeddingClient = EmbeddingClient(aiClient),
    private val embeddingService: EmbeddingService = EmbeddingService(),
    private val service: ChapterComposerService = ChapterComposerService(),
    private val contentService: ContentService = ContentService(),
    private val dataLogService: DataLogService = DataLogService()
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
            val model = embeddingService.readOrCreateModel("text-embedding-004", "Article Summary")
            while (true) {
                try {
                    readNextSignal(model)
                } catch (e: Exception) {
                    console.logError(e.stackTraceToString())
                }
                delay(10)
            }
        }
    }

    private fun excludeUntil(pageId: Long, duration: Duration) =
        excludedIds.add(pageId to Clock.System.now() + duration)

    private suspend fun readNextSignal(model: EmbeddingFamily) {
        val now = Clock.System.now()
        val excludedIds = this.excludedIds.filter { it.second > now }.map { it.first }
        setState { it.copy(exclusions = excludedIds.size) }

        val origin = service.findNextSignal(excludedIds)
        if (origin == null) {
            setState { it.copy(emptySignals = it.emptySignals + 1) }
            delay(1.minutes)
            return
        }

        setState { it.copy(signalDate = origin.page.seenAt) }
        if (origin.page.contentType == ContentType.NewsArticle) {
            // console.log("found secondary signal ${origin.page.id}")
            setState { it.copy(secondarySignals = it.secondarySignals + 1) }
            findSecondaryBucket(origin, model)
        } else {
            // console.log("found primary signal: ${origin.page.id}")
            setState { it.copy(primarySignals = it.primarySignals + 1) }
            findPrimaryBucket(origin, model)
        }
    }

    private suspend fun findPrimaryBucket(origin: ChapterPageSignal, model: EmbeddingFamily) {
        excludeUntil(origin.page.id, 1.hours)
        val signals = service.readInboundSignals(origin.page.id)
        if (signals.isEmpty()) {
            // console.log("primary bucket empty")
            return
        }
        val bucket = ChapterBucket()
        for (signal in signals) {
            if (signal.page.contentType != ContentType.NewsArticle) continue
            if (bucket.contains(signal.page.id)) continue
            val vector = readOrFetchVector(signal.page, model) ?: continue
            bucket.add(signal, vector)
        }
        bucket.shake()
        if (bucket.size < CHAPTER_MIN_ARTICLES) {
            // console.log("primary bucket too small after shake")
            return
        }

        if (findRelatedBucketAndMerge(bucket, model)) {
            return
        }
        // console.log("adding primary bucket")
        createChapter(bucket)
    }

    private suspend fun findSecondaryBucket(signal: ChapterPageSignal, model: EmbeddingFamily) {
        val vector = readOrFetchVector(signal.page, model)
        if (vector == null) {
            excludeUntil(signal.page.id, Duration.INFINITE)
             console.log("vector unavailable")
            return
        }

        val buckets = findBuckets(listOf(signal), 0, vector, signal.page.existedAt, CHAPTER_MAX_DISTANCE, model)
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

    private suspend fun findRelatedBucketAndMerge(bucket: ChapterBucket, model: EmbeddingFamily): Boolean {
        val buckets = findBuckets(
            originSignals = bucket.signals,
            chapterId = bucket.chapterId ?: 0,
            vector = bucket.averageVector,
            happenedAt = bucket.happenedAt,
            maxDistance = CHAPTER_MAX_DISTANCE / 2,
            model = model
        )
        if (buckets.isEmpty()) {
            return false
        }
        val sampleMaxSize = buckets.maxBy { it.size }.size
        val existingBucket = buckets.minBy { it.getDistanceVector(bucket, sampleMaxSize).priorityMagnitude }
        val originalSize = existingBucket.size
        val (smaller, bigger) = when {
            existingBucket.size < bucket.size && bucket.chapterId != null -> existingBucket to bucket
            else -> bucket to existingBucket
        }
        smaller.mergeInto(bigger)
        bigger.shake()
        updateChapter(bigger)
        deleteChapter(smaller)
        return true
    }

    private suspend fun createChapter(bucket: ChapterBucket) {
        val sources = bucket.getSecondarySources() + bucket.getPrimarySources()
        val chapterId = service.createChapter(
            chapter = Chapter(
                score = bucket.getChapterScore(),
                size = 1,
                cohesion = 1f,
                createdAt = Clock.System.now(),
                averageAt = bucket.happenedAt,
                storyDistance = null
            ),
            sources = sources,
            vector = bucket.averageVector
        )
        // console.log("created chapter! $chapterId")
        setState { it.copy(chaptersCreated = it.chaptersCreated + 1) }
    }

    private suspend fun updateChapter(bucket: ChapterBucket) {
        val sources = bucket.getPrimarySources() + bucket.getSecondarySources()

        service.updateChapterAndSources(
            chapterId = bucket.chapterId!!,
            score = bucket.getChapterScore(),
            size = bucket.size,
            cohesion = bucket.cohesion,
            happenedAt = bucket.happenedAt,
            sources = sources,
            vector = bucket.averageVector
        )

        console.log("updated chapter! size: ${bucket.size}, id:${bucket.chapterId}")
        setState { it.copy(chaptersUpdated = it.chaptersUpdated + 1) }
    }

    private suspend fun deleteChapter(bucket: ChapterBucket): Boolean {
        val chapterId = bucket.chapterId ?: return false
        console.log("deleted chapter: $chapterId")
        service.deleteChapter(chapterId)
        setState { it.copy(chaptersDeleted = it.chaptersDeleted + 1) }
        return true
    }

    private suspend fun findBuckets(
        originSignals: List<ChapterPageSignal>,
        chapterId: Long,
        vector: FloatArray,
        happenedAt: Instant,
        maxDistance: Float,
        model: EmbeddingFamily
    ): List<ChapterBucket> {
        val pageIds = originSignals.map { it.page.id }
        val chapterSignals = service.findTextRelatedChapters(chapterId, pageIds, vector)
        return chapterSignals.mapNotNull { (chapter, textDistance) ->
            val timeDistance = happenedAt.chapterDistanceTo(chapter.averageAt)
            val bucketDistance = BucketDistance(textDistance, timeDistance, 0f, 0f)
            if (bucketDistance.magnitude > maxDistance) return@mapNotNull null
            val bucket = ChapterBucket(chapter)
            val sourceSignals = service.readChapterSourceSignals(chapter.id)
            for (signal in sourceSignals) {
                val vector = readOrFetchVector(signal.page, model) ?: error("unable to find expected vector")
                bucket.add(signal, vector)
            }
            bucket
        }
    }

    private suspend fun readOrFetchVector(page: Page, model: EmbeddingFamily): FloatArray? {
        val cachedVector = embeddingService.readEmbedding(page.id, model.id)
        if (cachedVector != null) {
            return cachedVector
        }
        val summary = contentService.readSummaryContent(page.id) ?: articleReader.readArticle(page)
        if (summary == null) {
            setState { it.copy(contentsMissing = it.contentsMissing + 1) }
            console.log("Article summary missing")
            return null
        }
        // console.log("fetching vector")
        setState { it.copy(vectorsFetched = it.vectorsFetched + 1) }
        val vector = embeddingClient.fetchVector(page, model.model, summary) ?: error("unable to fetch vector")
        embeddingService.insertEmbedding(page.id, model.id, vector)
        return vector
    }
}
