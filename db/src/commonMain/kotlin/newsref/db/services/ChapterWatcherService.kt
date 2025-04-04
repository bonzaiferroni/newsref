package newsref.db.services

import newsref.db.DbService
import newsref.db.model.*
import newsref.db.tables.*
import newsref.model.data.Relevance
import newsref.model.data.SourceType
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
        val idCount = ChapterPageTable.chapterId.count()
        ChapterPageTable.leftJoin(ChapterTable)
            .select(ChapterPageTable.chapterId, idCount)
            .where {
                ChapterPageTable.relevance.isNull() and
                        ChapterPageTable.type.eq(SourceType.Article) and
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
                        ChapterPageTable.type.eq(SourceType.Article)
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
}