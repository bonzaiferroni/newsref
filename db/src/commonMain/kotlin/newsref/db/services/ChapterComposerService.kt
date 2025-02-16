package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.*
import newsref.db.core.cosineDistance
import newsref.db.model.Chapter
import newsref.db.model.ChapterSource
import newsref.db.model.ChapterSourceType
import newsref.db.model.Source
import newsref.db.tables.*
import newsref.db.utils.toLocalDateTimeUtc
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.time.Duration.Companion.days

class ChapterComposerService : DbService() {

    suspend fun findNearestParent(chapter: Chapter) = dbQuery {
        val time = chapter.happenedAt.toLocalDateTimeUtc()
        val vector = ChapterTable.select(ChapterTable.vector)
            .where { ChapterTable.id.eq(chapter.id) }
            .firstOrNull()?.let { it[ChapterTable.vector] } ?: return@dbQuery null
        val distance = ChapterTable.vector.cosineDistance(vector).alias("cosine_distance")
        ChapterTable.select(chapterColumns + distance)
            .where { ChapterTable.id.neq(chapter.id) and ChapterTable.happenedAt.less(time) }
            .orderBy(distance, SortOrder.ASC)
            .firstOrNull()?.let { StorySignal(it[distance], it.toChapter()) }
    }

    suspend fun setParent(chapterId: Long, parentId: Long) = dbQuery {
        ChapterTable.update({ChapterTable.id.eq(chapterId)}) {
            it[ChapterTable.parentId] = parentId
        }
    }

    suspend fun readParentIsNull() = dbQuery {
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.parentId.eq(null) or ChapterTable.storyId.eq(null) }
            .map { it.toChapter() }
    }

    suspend fun readChildren(parentId: Long) = dbQuery {
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.parentId.eq(parentId) }
            .map { it.toChapter() }
    }

    suspend fun findNextSignal(excludedIds: List<Long>) = dbQuery {
        SourceTable.leftJoin(ChapterSourceTable).select(SourceTable.columns)
            .where {
                ChapterSourceTable.sourceId.isNull() and
                        SourceTable.score.greaterEq(2) and
                        SourceTable.id.notInList(excludedIds)
            }
            .orderBy(SourceTable.seenAt, SortOrder.DESC)
            .firstOrNull()?.toSource()?.let { source ->
                val outboundIds = LinkTable.leftJoin(LeadTable).select(LeadTable.sourceId)
                    .where { LinkTable.sourceId.eq(source.id) and LinkTable.isExternal.eq(true) }
                    .map { it[LeadTable.sourceId]?.value }
                val idSet = outboundIds.groupBy { it }.keys.mapNotNull { it }.toSet()
                ChapterSignal(source, idSet)
            }
    }

    suspend fun readConcurrentTopSources(time: Instant) = dbQuery {
        SourceTable.select(SourceTable.columns)
            .where {
                SourceTable.withinTimeRange(time, time + CHAPTER_EPOCH) and
                        SourceTable.contentCount.greaterEq(EMBEDDING_MIN_WORDS) and
                        (SourceTable.score.greaterEq(1) or SourceTable.feedPosition.less(10))
            }
            .map { it.toSource() }
    }

    suspend fun readInboundSignals(sourceId: Long) = dbQuery {
        LinkTable.leftJoin(LeadTable).join(SourceTable, JoinType.LEFT, LinkTable.sourceId, SourceTable.id)
            .select(SourceTable.columns)
            .where { LeadTable.sourceId.eq(sourceId) and SourceTable.contentCount.greaterEq(EMBEDDING_MIN_WORDS) }
            .map { it.toChapterSignal() }
    }

    suspend fun findPrimarySources(sourceIds: List<Long>) = dbQuery {
        val ids = LinkTable.leftJoin(LeadTable).select(LeadTable.sourceId)
            .where {
                LinkTable.isExternal.eq(true) and LinkTable.sourceId.inList(sourceIds) and
                        LeadTable.sourceId.isNotNull()
            }
            .map { it[LeadTable.sourceId]!!.value }
        val primaryIds = ids.groupingBy { it }.eachCount().filter { it.value >= 3 }.map { it.key }
        SourceTable.select(SourceTable.columns)
            .where { SourceTable.id.inList(primaryIds) }
            .map { it.toSource() }
    }

    suspend fun createChapter(chapter: Chapter, sources: List<ChapterSource>, vector: FloatArray,) = dbQuery {
        val chapterId = ChapterTable.insertAndGetId {
            it.fromModel(chapter)
            it[ChapterTable.vector] = vector
        }.value
        deleteAndInsertSources(chapterId, sources)
        chapterId
    }

    suspend fun updateChapterDescription(
        chapter: Chapter,
    ) = dbQuery {
        ChapterTable.update({ ChapterTable.id.eq(chapter.id) }) {
            it[ChapterTable.title] = chapter.title
            it[ChapterTable.summary] = chapter.summary
        }
    }

    suspend fun updateChapterAndSources(
        chapterId: Long,
        score: Int,
        size: Int,
        cohesion: Float,
        happenedAt: Instant,
        sources: List<ChapterSource>,
        vector: FloatArray,
    ) = dbQuery {
        ChapterTable.update({ ChapterTable.id.eq(chapterId) }) {
            it[ChapterTable.score] = score
            it[ChapterTable.size] = size
            it[ChapterTable.cohesion] = cohesion
            it[ChapterTable.happenedAt] = happenedAt.toLocalDateTimeUtc()
            it[ChapterTable.vector] = vector
            it[ChapterTable.parentId] = null
            if ((0..9).random() == 0)
                it[ChapterTable.title] = null
        }
        deleteAndInsertSources(chapterId, sources)
        unlinkChildren(chapterId)
    }

    fun Transaction.deleteAndInsertSources(chapterId: Long, sources: List<ChapterSource>) {
        ChapterSourceTable.deleteWhere { ChapterSourceTable.chapterId eq chapterId }
        ChapterSourceTable.batchInsert(sources) {
            this[ChapterSourceTable.chapterId] = chapterId
            this[ChapterSourceTable.sourceId] = it.sourceId
            this[ChapterSourceTable.type] = it.type
            this[ChapterSourceTable.distance] = it.distance
            this[ChapterSourceTable.linkDistance] = it.linkDistance
            this[ChapterSourceTable.timeDistance] = it.timeDistance
            this[ChapterSourceTable.textDistance] = it.textDistance
        }
    }

    fun Transaction.unlinkChildren(chapterId: Long) {
        val childIds = ChapterTable.select(ChapterTable.id)
            .where { ChapterTable.parentId.eq(chapterId) }
            .map { it[ChapterTable.id].value }

        for (childId in childIds) {
            ChapterTable.update({ChapterTable.id.eq(childId)}) {
                it[ChapterTable.parentId] = null
            }
        }
    }

    suspend fun readChapters(limit: Int = 100) = dbQuery {
        ChapterTable.select(chapterColumns)
            .orderBy(ChapterTable.score, SortOrder.DESC)
            .limit(limit)
            .map { it.toChapter() }
    }

    suspend fun readChapter(chapterId: Long) = dbQuery {
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.toChapter()
    }

    suspend fun readChapterSourceInfos(chapterId: Long) = dbQuery {
        ChapterSourceTable.leftJoin(SourceTable)
            .select(ChapterSourceTable.columns + SourceTable.columns)
            .where { ChapterSourceTable.chapterId.eq(chapterId) }
            .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
            .map { it.toChapterSourceInfo() }
    }

    suspend fun readTitleIsNull() = dbQuery {
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.title.isNull() }
            .map { it.toChapter() }
    }

    suspend fun readCurrentChapters(epochs: Int) = dbQuery {
        val time = (Clock.System.now() - CHAPTER_EPOCH * epochs).toLocalDateTimeUtc()
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.happenedAt.greater(time) }
            .map { it.toChapter() }
    }

    suspend fun readChapterSignals(chapterId: Long) = dbQuery {
        ChapterSourceTable.leftJoin(SourceTable).select(SourceTable.columns)
            .where {
                ChapterSourceTable.chapterId.eq(chapterId) and
                        ChapterSourceTable.type.eq(ChapterSourceType.Secondary)
            }
            .map { it.toChapterSignal() }
    }

    suspend fun deleteChapter(chapterId: Long) = dbQuery {
        ChapterTable.deleteWhere { ChapterTable.id.eq(chapterId) }
    }

    suspend fun deleteChapterSource(chapterId: Long, sourceId: Long) = dbQuery {
        ChapterSourceTable.deleteWhere {
            ChapterSourceTable.chapterId.eq(chapterId) and ChapterSourceTable.sourceId.eq(sourceId)
        }
    }
}

internal fun ResultRow.toChapterSignal() = this.let {
    val source = this.toSource()
    val outboundIds = LinkTable.leftJoin(LeadTable).select(LeadTable.sourceId)
        .where {
            LinkTable.sourceId.eq(source.id) and
                    LinkTable.isExternal.eq(true) and
                    LeadTable.sourceId.isNotNull()
        }
        .groupBy(LeadTable.sourceId)
        .map { it[LeadTable.sourceId]!!.value }
        .toSet()
    ChapterSignal(source, outboundIds)
}

const val CHAPTER_MIN_ARTICLES = 3
const val ORIGIN_MIN_SCORE = MAX_LINK_SIGNAL * CHAPTER_MIN_ARTICLES
const val CHAPTER_MAX_DISTANCE = .5f
const val CHAPTER_MERGE_FACTOR = .75f
val CHAPTER_EPOCH = 5.days

data class ChapterSignal(
    val source: Source,
    val linkIds: Set<Long>
)

fun List<Instant>.averageInstant() = Instant.fromEpochSeconds(this.sumOf { it.epochSeconds } / this.size)