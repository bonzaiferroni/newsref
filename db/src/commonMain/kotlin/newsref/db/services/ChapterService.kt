package newsref.db.services

import newsref.db.*
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*

class ChapterService : DbService() {

    suspend fun findOrigin() = dbQuery {
        SourceTable.leftJoin(ChapterSourceTable)
            .select(SourceTable.columns)
            .where { ChapterSourceTable.sourceId.isNull() and SourceTable.score.greater(ORIGIN_MIN_SCORE) }
            .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
            .firstOrNull()?.toSource()
    }

    suspend fun findInboundSources(sourceId: Long) = dbQuery {
        LinkTable.leftJoin(LeadTable).join(SourceTable, JoinType.LEFT, LinkTable.sourceId, SourceTable.id)
            .select(SourceTable.columns)
            .where { LeadTable.sourceId.eq(sourceId) and SourceTable.contentCount.greaterEq(VECTOR_MIN_WORDS)  }
            .map { it.toSource() }
    }

    suspend fun findPrimarySources(sourceIds: List<Long>) = dbQuery {
        val ids = LinkTable.leftJoin(LeadTable)
            .select(LeadTable.sourceId)
            .where { LinkTable.sourceId.inList(sourceIds) and LeadTable.sourceId.isNotNull() }
            .map { it[LeadTable.sourceId]!!.value }
        val primaryIds = ids.groupingBy { it }.eachCount().filter { it.value > 1 }.map { it.key }
        SourceTable
            .select(SourceTable.columns)
            .where { SourceTable.id.inList(primaryIds) }
            .map { it.toSource() }
    }
}

const val ORIGIN_MIN_SCORE = 6