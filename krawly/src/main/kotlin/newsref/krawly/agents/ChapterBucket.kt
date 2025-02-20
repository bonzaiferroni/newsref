package newsref.krawly.agents

import kotlinx.datetime.Instant
import newsref.db.*
import newsref.db.model.Chapter
import newsref.db.services.*
import newsref.db.utils.*
import newsref.db.utils.distance
import newsref.db.model.ChapterSource
import newsref.db.model.ChapterSourceType
import newsref.db.model.Relevance
import kotlin.collections.maxBy
import kotlin.collections.set
import kotlin.math.abs
import kotlin.math.sqrt

private val console = globalConsole.getHandle("ChapterBucket")

class ChapterBucket(
    private var _chapter: Chapter? = null
) {
    private var _chapterId: Long? = null
    private val _signals = mutableListOf<ChapterSourceSignal>()
    private val _vectors = mutableMapOf<Long, FloatArray>()
    private var _averageVector: FloatArray? = null
    private var _happenedAt: Instant? = null
    private val _linkTally = mutableMapOf<Long, Int>()
    private var _cohesion: Float? = null
    private var _distances: Map<Long, Float>? = null
    private var _relevantIds = mutableSetOf<Long>()
    private var _irrelevantIds = mutableSetOf<Long>()

    val signals: List<ChapterSourceSignal> get() = _signals
    val vectors: Map<Long, FloatArray> get() = _vectors
    val distances: Map<Long, Float> get() = _distances ?: findDistances().also { _distances = it }
    val size get() = _signals.size
    val averageVector: FloatArray get() = _averageVector ?: findAverageVector().also { _averageVector = it }
    val happenedAt: Instant get() = _happenedAt ?: findHappenedAt().also { _happenedAt = it }
    val cohesion: Float get() = _cohesion ?: findCohesion().also { _cohesion = it }
    val chapterId get() = _chapterId ?: _chapter?.id
    val title get() = _chapter?.title
    val linkIds: Set<Long> get() = _linkTally.keys
    val relevantIds: Set<Long> get() = _relevantIds
    val irrelevantIds: Set<Long> get() = _irrelevantIds

    fun remove(sourceId: Long) {
        if (relevantIds.contains(sourceId)) error("removed relevant signal")
        val signal = _signals.firstOrNull { it.source.id == sourceId }
        if (signal == null) return
        invalidateCache()
        _vectors.remove(sourceId)
        _signals.removeIf { it.source.id == sourceId }
        for (id in signal.linkIds) {
            _linkTally[id] = _linkTally.getValue(id) - 1
            if (_linkTally.getValue(id) == 0) _linkTally.remove(id)
        }
    }

    fun add(signal: ChapterSourceSignal, vector: FloatArray) {
        if (irrelevantIds.contains(signal.source.id)) error("added irrelevant signal")
        if (_vectors.contains(signal.source.id)) error("signal is already present in bucket")
        invalidateCache()
        _signals.add(signal)
        _vectors[signal.source.id] = vector
        for (id in signal.linkIds) {
            _linkTally[id] = (_linkTally[id] ?: 0) + 1
        }
    }

    fun contains(sourceId: Long) = _signals.any { it.source.id == sourceId }

    fun getSourceIds() = signals.map { it.source.id }

    fun getChapterScore() = signals.sumOf { it.source.score ?: 0 }

    fun getDistanceVector(signal: ChapterSourceSignal, vector: FloatArray, sampleMaxSize: Int = 0) =
        getDistanceVector(signal.source.existedAt, signal.linkIds, vector, sampleMaxSize)

    fun getDistanceVector(bucket: ChapterBucket, sampleMaxSize: Int = 0) =
        getDistanceVector(bucket.happenedAt, bucket.linkIds, bucket.averageVector, sampleMaxSize)

    fun getDistanceVector(
        time: Instant,
        outbounds: Set<Long>,
        vector: FloatArray,
        sampleMaxSize: Int = 0,
    ): BucketDistance {
        val embeddingDistance = distance(vector, averageVector) * DISTANCE_EMBEDDING_WEIGHT
        val timeDistance = timeDistance(time) * DISTANCE_TIME_WEIGHT
        val outboundDistance = outboundDistance(outbounds) * DISTANCE_OUTBOUND_WEIGHT
        val sizeDistance = when {
            sampleMaxSize > 0 -> sizeDistance(sampleMaxSize) * DISTANCE_SIZE_WEIGHT
            else -> 0f
        }
        return BucketDistance(embeddingDistance, timeDistance, outboundDistance, sizeDistance)
    }

    fun shake(): List<Long> {
        if (size == 0) return emptyList()
        val removedIds = mutableListOf<Long>()
        val initialOutboundIds = linkIds.toSet()
        do {
            val (distance, signal) = signals.filter { it.chapterSource?.relevance != Relevance.Relevant }
                .map {
                    val distance = getDistanceVector(it, vectors.getValue(it.source.id)).magnitude
                    distance to it
                }.maxBy { it.first }
            if (distance < CHAPTER_MAX_DISTANCE) break
            remove(signal.source.id)
            removedIds.add(signal.source.id)
        } while (size > 1)
        removedIds.addAll(initialOutboundIds - linkIds)
        if (removedIds.isNotEmpty()) {
            console.log("${removedIds.size} shaken out of bucket")
        }
        return removedIds
    }

    fun getPrimarySources() = linkIds.map {
        ChapterSource(
            chapterId = chapterId ?: 0,
            sourceId = it,
            type = ChapterSourceType.Primary,
            distance = null,
            textDistance = null,
            linkDistance = null,
            timeDistance = null,
        )
    }

    fun getSecondarySources() = signals.map {
        ChapterSource(
            chapterId = chapterId ?: 0,
            sourceId = it.source.id,
            type = ChapterSourceType.Secondary,
            distance = distances.getValue(it.source.id),
            textDistance = contentDistance(it),
            linkDistance = outboundDistance(it.linkIds),
            timeDistance = timeDistance(it.source.existedAt),
        )
    }

    fun setId(chapterId: Long) {
        _chapterId = chapterId
    }

    fun updateRelevance(relevantIds: Set<Long>, irrelevantIds: Set<Long>) {
        _relevantIds.clear()
        _relevantIds.addAll(relevantIds)
        _irrelevantIds.clear()
        _irrelevantIds.addAll(irrelevantIds)
        val removeIds = irrelevantIds.filter { _vectors.contains(it) }
        if (removeIds.isEmpty()) return
        for (id in removeIds) {
            remove(id)
        }
        console.log("removed ${removeIds.size} irrelevant signals")
    }

    fun mergeInto(bucket: ChapterBucket) {
        for (signal in signals) {
            if (bucket.contains(signal.source.id)) continue
            if (bucket.irrelevantIds.contains(signal.source.id)) continue
            val vector = vectors.getValue(signal.source.id)
            bucket.add(signal, vector)
        }
    }

    private fun invalidateCache() {
        _averageVector = null
        _happenedAt = null
        _distances = null
        _cohesion = null
    }

    private fun contentDistance(signal: ChapterSourceSignal) = distance(averageVector, vectors.getValue(signal.source.id))

    private fun findAverageVector() = averageAndNormalize(vectors.values.toList())

    private fun findHappenedAt() = signals.map { it.source.existedAt }.averageInstant()

    private fun findDistances() = signals.associate {
        it.source.id to getDistanceVector(it, vectors.getValue(it.source.id)).magnitude
    }

    private fun findCohesion() = distances.values.sumOf { it.toDouble() }.let { it / size }.toFloat()

    private fun outboundDistance(linkIds: Set<Long>): Float {
        if (linkIds.isEmpty()) return 0f
        return linkIds.sumOf {
            1 - (_linkTally[it] ?: 0) / size.toDouble()
        }.let { it / linkIds.size }.toFloat()
    }

    private fun timeDistance(instant: Instant) = happenedAt.chapterDistanceTo(instant)

    private fun sizeDistance(sampleMaxSize: Int) = (sampleMaxSize - size) / sampleMaxSize.toFloat()
}

data class BucketDistance(
    val text: Float,
    val time: Float,
    val link: Float,
    val size: Float,
) {
    val magnitude get() = sqrt(text * text + time * time)
    val priorityMagnitude get() = sqrt(text * text + time * time + link * link + size * size)
}

const val DISTANCE_EMBEDDING_WEIGHT = 1.0F
const val DISTANCE_TIME_WEIGHT = 1.0F
const val DISTANCE_OUTBOUND_WEIGHT = 0.1F
const val DISTANCE_SIZE_WEIGHT = 0.1F

fun Instant.chapterDistanceTo(instant: Instant) =
    (abs((instant - this).inWholeHours / 24.0) / CHAPTER_EPOCH.inWholeDays).toFloat()