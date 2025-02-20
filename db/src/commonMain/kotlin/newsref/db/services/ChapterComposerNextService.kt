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

class ChapterComposerNextService : DbService() {
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

    suspend fun findTextRelatedChapters(sourceId: Long, vector: FloatArray) = dbQuery {
        val distance = ChapterTable.vector.cosineDistance(vector).alias("cosine_distance")
        val subquery = ChapterSourceTable.select(ChapterSourceTable.chapterId)
            .where {
                ChapterSourceTable.relevance.eq(Relevance.Irrelevant) and
                        ChapterSourceTable.sourceId.eq(sourceId)
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
                    relevance.isNullOrEq(Relevance.Unsure)
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
}