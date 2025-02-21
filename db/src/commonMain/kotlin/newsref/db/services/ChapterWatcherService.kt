package newsref.db.services

import newsref.db.DbService
import newsref.db.model.*
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ChapterWatcherService : DbService() {

    suspend fun readChapter(chapterId: Long) = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.toChapter()
    }

    suspend fun readTopNullTitle() = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.title.isNull() and ChapterTable.size.greaterEq(CHAPTER_MIN_ARTICLES) }
            .orderBy(ChapterTable.score, SortOrder.DESC)
            .firstOrNull()?.toChapter()
    }

    suspend fun readTopNullRelevance() = dbQuery {
        val idCount = ChapterSourceTable.chapterId.count()
        ChapterSourceTable.leftJoin(ChapterTable)
            .select(ChapterSourceTable.chapterId, idCount)
            .where {
                ChapterSourceTable.relevance.isNull() and
                        ChapterSourceTable.type.eq(NewsSourceType.Secondary) and
                        ChapterTable.title.isNotNull()
            }
            .groupBy(ChapterSourceTable.chapterId)
            .orderBy(idCount, SortOrder.DESC)
            .firstOrNull()?.let { Pair(it[ChapterSourceTable.chapterId].value, it[idCount]) }
    }

    suspend fun readChapterSourceInfos(chapterId: Long) = dbQuery {
        ChapterSourceTable.leftJoin(PageTable)
            .select(ChapterSourceTable.columns + PageTable.columns)
            .where { ChapterSourceTable.chapterId.eq(chapterId) }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .map { it.toChapterSourceInfo() }
    }

    suspend fun readNullRelevanceChapterSourceInfos(chapterId: Long) = dbQuery {
        ChapterSourceTable.leftJoin(PageTable)
            .select(ChapterSourceTable.columns + PageTable.columns)
            .where {
                ChapterSourceTable.chapterId.eq(chapterId) and
                        ChapterSourceTable.relevance.isNull() and
                        ChapterSourceTable.type.eq(NewsSourceType.Secondary)
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

    suspend fun updateChapterSourceRelevance(chapterSources: List<ChapterSource>) = dbQuery {
        for (chapterSource in chapterSources) {
            if (chapterSource.relevance == Relevance.Irrelevant) {
                ChapterSourceTable.deleteWhere { id.eq(chapterSource.id) }
                ChapterExclusionTable.insert {
                    it[chapterId] = chapterSource.chapterId
                    it[sourceId] = chapterSource.sourceId
                }
            } else {
                ChapterSourceTable.update({
                    ChapterSourceTable.sourceId.eq(chapterSource.sourceId) and
                            ChapterSourceTable.chapterId.eq(chapterSource.chapterId)
                }) {
                    it[relevance] = chapterSource.relevance
                }
            }
        }
    }
}