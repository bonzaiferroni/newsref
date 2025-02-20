@file:Suppress("DuplicatedCode")

package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.*
import newsref.db.core.*
import newsref.db.model.*
import newsref.db.tables.*
import newsref.db.utils.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import kotlin.time.Duration.Companion.days

class ChapterComposerService : DbService() {
    suspend fun findNextSignal(excludedIds: List<Long>) = dbQuery {
        val subquery = ChapterSourceTable.select(ChapterSourceTable.sourceId)
            .where { ChapterSourceTable.relevance.isNullOrNeq(Relevance.Irrelevant) }
        SourceTable.leftJoin(ChapterSourceTable).select(SourceTable.columns + ChapterSourceTable.columns)
            .where {
                SourceTable.score.greaterEq(2) and
                        SourceTable.id.notInList(excludedIds) and
                        SourceTable.id.notInSubQuery(subquery)
            }
            .orderBy(SourceTable.seenAt, SortOrder.DESC)
            .firstOrNull()?.toChapterSignal()
    }

    suspend fun findTextRelatedChapters(sourceIds: List<Long>, vector: FloatArray) = dbQuery {
        val distance = ChapterTable.vector.cosineDistance(vector).alias("cosine_distance")
        val subquery = ChapterSourceTable.select(ChapterSourceTable.chapterId)
            .where {
                ChapterSourceTable.relevance.eq(Relevance.Irrelevant) and
                        ChapterSourceTable.sourceId.inList(sourceIds)
            }
        ChapterTable.select(ChapterAspect.columns + distance)
            .where { ChapterTable.happenedAt.since(CHAPTER_EPOCH * 4) and ChapterTable.id.notInSubQuery(subquery) }
            .map { it.toChapter() to it[distance] }
            .filter { it.second < CHAPTER_MAX_DISTANCE }
    }

    suspend fun readChapterSourceSignals(chapterId: Long) = dbQuery {
        ChapterSourceTable.leftJoin(SourceTable)
            .select(SourceTable.columns + ChapterSourceTable.columns)
            .where {
                ChapterSourceTable.chapterId.eq(chapterId) and
                        ChapterSourceTable.type.eq(ChapterSourceType.Secondary) and
                        ChapterSourceTable.relevance.isNullOrNeq(Relevance.Irrelevant)
            }
            .map { it.toChapterSignal() }
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
        }
        ChapterTable.update({ChapterTable.parentId.eq(chapterId)}) {
            it[ChapterTable.parentId] = null
        }
        updateAndTrimSources(chapterId, sources)
    }

    fun Transaction.updateAndTrimSources(chapterId: Long, sources: List<ChapterSource>) {
        val sourceIds = sources.map { it.sourceId }
        ChapterSourceTable.deleteWhere {
            ChapterSourceTable.chapterId.eq(chapterId) and
                    sourceId.notInList(sourceIds) and
                    relevance.isNullOrNeq(Relevance.Irrelevant)
        }
        for (source in sources) {
            ChapterSourceTable.upsert(
                ChapterSourceTable.chapterId, ChapterSourceTable.sourceId
            ) {
                it[ChapterSourceTable.chapterId] = chapterId
                it[sourceId] = source.sourceId
                it[type] = source.type
                it[distance] = source.distance
                it[linkDistance] = source.linkDistance
                it[timeDistance] = source.timeDistance
                it[textDistance] = source.textDistance
            }
        }
    }

    suspend fun deleteChapter(chapterId: Long) = dbQuery {
        ChapterTable.deleteWhere { ChapterTable.id.eq(chapterId) }
    }

    suspend fun createChapter(chapter: Chapter, sources: List<ChapterSource>, vector: FloatArray,) = dbQuery {
        val chapterId = ChapterTable.insertAndGetId {
            it.fromModel(chapter)
            it[ChapterTable.vector] = vector
        }.value
        updateAndTrimSources(chapterId, sources)
        chapterId
    }

    suspend fun readInboundSignals(sourceId: Long) = dbQuery {
        LinkTable.leftJoin(LeadTable).join(SourceTable, JoinType.LEFT, LinkTable.sourceId, SourceTable.id)
            .select(SourceTable.columns)
            .where { LeadTable.sourceId.eq(sourceId) and SourceTable.contentCount.greaterEq(EMBEDDING_MIN_WORDS) }
            .map { it.toChapterSignal() }
    }
}

internal fun ResultRow.toChapterSignal() = this.let {
    val source = this.toSource()
    val chapterSource = when {
        this.getOrNull(ChapterSourceTable.id) != null -> this.toChapterSource()
        else -> null
    }
    val outboundIds = LinkTable.leftJoin(LeadTable).select(LeadTable.sourceId)
        .where {
            LinkTable.sourceId.eq(source.id) and
                    LinkTable.isExternal.eq(true) and
                    LeadTable.sourceId.isNotNull()
        }
        .groupBy(LeadTable.sourceId)
        .map { it[LeadTable.sourceId]!!.value }
        .toSet()
    ChapterSourceSignal(source, chapterSource, outboundIds)
}

const val CHAPTER_MIN_ARTICLES = 3
const val ORIGIN_MIN_SCORE = MAX_LINK_SIGNAL * CHAPTER_MIN_ARTICLES
const val CHAPTER_MAX_DISTANCE = .5f
const val CHAPTER_MERGE_FACTOR = .75f
val CHAPTER_EPOCH = 5.days

data class ChapterSourceSignal(
    val source: Source,
    val chapterSource: ChapterSource?,
    val linkIds: Set<Long>
)

fun List<Instant>.averageInstant() = Instant.fromEpochSeconds(this.sumOf { it.epochSeconds } / this.size)