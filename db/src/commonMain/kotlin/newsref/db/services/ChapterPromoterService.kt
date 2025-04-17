package newsref.db.services

import klutch.db.DbService
import newsref.db.globalConsole
import newsref.db.log.toYellow
import newsref.db.model.*
import newsref.db.tables.*
import newsref.db.utils.read
import newsref.db.utils.toSqlString
import newsref.db.utils.updateById
import newsref.model.data.Relevance
import newsref.model.data.SourceType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

private val console = globalConsole.getHandle(ChapterPromoterService::class)

class ChapterPromoterService : DbService() {

    suspend fun readNextPromotion() = dbQuery {
        val promotionReady = Op.build {
            promotionTargets.map { ChapterTable.level.eq(it.key) and ChapterTable.size.greaterEq(it.value) }
                .reduce { acc, op -> acc or op }
        }
        ChapterTable.read { promotionReady }
            .orderBy(ChapterTable.size, SortOrder.DESC)
            .firstOrNull()?.toChapter()
    }

    suspend fun readChapter(chapterId: Long) = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.toChapter()
    }

    suspend fun readTopNullRelevance() = dbQuery {
        val idCount = ChapterPageTable.chapterId.count()
        ChapterPageTable.leftJoin(ChapterTable)
            .select(ChapterPageTable.chapterId, idCount)
            .where {
                ChapterPageTable.relevance.isNull() and
                        ChapterPageTable.sourceType.eq(SourceType.Article) and
                        ChapterTable.title.isNotNull()
            }
            .groupBy(ChapterPageTable.chapterId)
            .orderBy(idCount, SortOrder.DESC)
            .firstOrNull()?.let { Pair(it[ChapterPageTable.chapterId].value, it[idCount]) }
    }

    suspend fun readChapterSourceInfos(chapterId: Long) = dbQuery {
        ChapterPageTable.leftJoin(PageTable)
            .select(ChapterPageTable.columns + PageTable.columns)
            .where { ChapterPageTable.chapterId.eq(chapterId) }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .map { it.toChapterSourceInfo() }
    }

    suspend fun readNullRelevanceChapterSourceInfos(chapterId: Long) = dbQuery {
        ChapterPageTable.leftJoin(PageTable)
            .select(ChapterPageTable.columns + PageTable.columns)
            .where {
                ChapterPageTable.chapterId.eq(chapterId) and
                        ChapterPageTable.relevance.isNull() and
                        ChapterPageTable.sourceType.eq(SourceType.Article)
            }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .map { it.toChapterSourceInfo() }
    }

    suspend fun updateChapterDescription(
        chapter: Chapter,
    ) = dbQuery {
        ChapterTable.update({ ChapterTable.id.eq(chapter.id) }) {
            it[title] = chapter.title
            it[summary] = chapter.summary
        }
    }

    suspend fun updateChapterSourceRelevance(chapterPages: List<ChapterPage>) = dbQuery {
        for (chapterSource in chapterPages) {
            if (chapterSource.relevance == Relevance.Irrelevant) {
                ChapterPageTable.deleteWhere { id.eq(chapterSource.id) }
                ChapterExclusionTable.upsert(ChapterExclusionTable.chapterId, ChapterExclusionTable.pageId) {
                    it[chapterId] = chapterSource.chapterId
                    it[pageId] = chapterSource.pageId
                }
            } else {
                ChapterPageTable.update({
                    ChapterPageTable.pageId.eq(chapterSource.pageId) and
                            ChapterPageTable.chapterId.eq(chapterSource.chapterId)
                }) {
                    it[relevance] = chapterSource.relevance
                }
            }
        }
    }

    suspend fun readPeopleIds(chapterId: Long) = dbQuery {
        ChapterPageTable.join(PagePersonTable, JoinType.INNER, ChapterPageTable.pageId, PagePersonTable.pageId)
            .select(PagePersonTable.personId)
            .where { ChapterPageTable.chapterId.eq(chapterId) }
            .map { it[PagePersonTable.personId].value }
    }

    suspend fun updateLevel(
        chapterId: Long,
        level: Int,
        title: String,
        locationId: Int?,
        personMentions: Map<Int, Int>
    ) = dbQuery {
        ChapterPersonTable.deleteWhere { this.chapterId.eq(chapterId) }
        ChapterPersonTable.batchInsert(personMentions.map { it.key to it.value }) {
            this[ChapterPersonTable.chapterId] = chapterId
            this[ChapterPersonTable.personId] = it.first
            this[ChapterPersonTable.mentions] = it.second
        }
        ChapterTable.updateById(chapterId) {
            it[this.level] = level
            it[this.title] = title
        }
    }

    suspend fun readLocationIds(chapterId: Long) = dbQuery {
        ChapterPageTable.leftJoin(PageTable).select(PageTable.locationId)
            .where { ChapterPageTable.chapterId.eq(chapterId) and PageTable.locationId.isNotNull() }
            .map { it[PageTable.locationId]!!.value }
    }
}

val promotionTargets = mapOf(
    0 to 3,
    1 to 10,
    2 to 25,
)