package newsref.db.services

import newsref.db.DbService
import newsref.db.model.*
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*

class ChapterWatcherService : DbService() {

    suspend fun readChapter(chapterId: Long) = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.toChapter()
    }

    suspend fun readTopNullTitle() = dbQuery {
        val table = ChapterTable
        ChapterTable.select(ChapterAspect.columns)
            .where { table.title.isNull() and table.size.greaterEq(CHAPTER_MIN_ARTICLES) }
            .orderBy(table.size, SortOrder.DESC)
            .firstOrNull()?.toChapter()
    }

    suspend fun readTopNullRelevance() = dbQuery {
        val table = ChapterSourceTable
        val idCount = table.chapterId.count()
        table.select(table.chapterId, idCount)
            .where { table.relevance.isNull() and table.type.eq(ChapterSourceType.Secondary) }
            .groupBy(table.chapterId)
            .orderBy(idCount, SortOrder.DESC)
            .firstOrNull()?.let { Pair(it[table.chapterId].value, it[idCount])}
    }

    suspend fun readChapterSourceInfos(chapterId: Long) = dbQuery {
        ChapterSourceTable.leftJoin(SourceTable)
            .select(ChapterSourceTable.columns + SourceTable.columns)
            .where { ChapterSourceTable.chapterId.eq(chapterId) }
            .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
            .map { it.toChapterSourceInfo() }
    }

    suspend fun readNullRelevanceChapterSourceInfos(chapterId: Long) = dbQuery {
        ChapterSourceTable.leftJoin(SourceTable)
            .select(ChapterSourceTable.columns + SourceTable.columns)
            .where { ChapterSourceTable.chapterId.eq(chapterId) and ChapterSourceTable.relevance.isNull() }
            .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
            .map { it.toChapterSourceInfo() }
    }

    suspend fun updateChapterDescription(
        chapter: Chapter,
    ) = dbQuery {
        ChapterTable.update({ ChapterTable.id.eq(chapter.id) }) {
            it[ChapterTable.title] = chapter.title
            it[ChapterTable.summary] = chapter.summary
        }
    }

    suspend fun updateChapterSourceRelevance(chapterSources: List<ChapterSource>) = dbQuery {
        for (chapterSource in chapterSources) {
            ChapterSourceTable.update({
                ChapterSourceTable.sourceId.eq(chapterSource.sourceId) and
                        ChapterSourceTable.chapterId.eq(chapterSource.chapterId)
            }) {
                it[relevance] = chapterSource.relevance
            }
        }
    }
}