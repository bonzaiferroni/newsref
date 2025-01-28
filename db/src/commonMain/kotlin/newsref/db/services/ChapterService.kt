package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.*
import newsref.db.tables.*
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.data.*
import org.jetbrains.exposed.sql.*
import kotlin.time.Duration.Companion.days

class ChapterService : DbService() {

    suspend fun findOrigin() = dbQuery {
        SourceTable.leftJoin(ChapterSourceTable).select(SourceTable.columns)
            .where { ChapterSourceTable.sourceId.isNull() and SourceTable.score.greater(ORIGIN_MIN_SCORE) }
            .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
            .firstOrNull()?.toSource()
    }

    suspend fun readConcurrentTopSources(time: Instant) = dbQuery {
        SourceTable.select(SourceTable.columns)
            .where {
                SourceTable.withinTimeRange(time, time + CHAPTER_EPOCH) and
                        SourceTable.contentCount.greaterEq(VECTOR_MIN_WORDS) and
                        SourceTable.score.greaterEq(MINIMUM_SCORE_RECORD)
            }
            .map { it.toSource() }
    }

    suspend fun readInboundSources(sourceId: Long) = dbQuery {
        LinkTable.leftJoin(LeadTable).join(SourceTable, JoinType.LEFT, LinkTable.sourceId, SourceTable.id)
            .select(SourceTable.columns)
            .where { LeadTable.sourceId.eq(sourceId) and SourceTable.contentCount.greaterEq(VECTOR_MIN_WORDS) }
            .map { it.toSource() }
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

    suspend fun addChapter(chapter: Chapter, primarySources: List<Source>, secondarySources: List<Source>) = dbQuery {

    }
}

const val ORIGIN_MIN_SCORE = 6
val CHAPTER_EPOCH = 3.days