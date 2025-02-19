package newsref.db.services

import newsref.db.DbService
import newsref.db.model.Chapter
import newsref.db.model.ChapterSource
import newsref.db.tables.ChapterSourceTable
import newsref.db.tables.ChapterSourceTable.chapterId
import newsref.db.tables.ChapterSourceTable.sourceId
import newsref.db.tables.ChapterTable
import newsref.db.tables.SourceTable
import newsref.db.tables.chapterColumns
import newsref.db.tables.toChapter
import newsref.db.tables.toChapterSourceInfo
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.update

class ChapterWatcherService : DbService() {

    suspend fun readChapter(chapterId: Long) = dbQuery {
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.toChapter()
    }

    suspend fun readTopNullTitle() = dbQuery {
        val table = ChapterTable
        ChapterTable.select(chapterColumns)
            .where { table.title.isNull() }
            .orderBy(table.size, SortOrder.DESC)
            .firstOrNull()?.toChapter()
    }

    suspend fun readTopNullRelevance() = dbQuery {
        val table = ChapterSourceTable
        val idCount = table.chapterId.count()
        table.select(table.chapterId, idCount)
            .where { table.relevance.isNull() }
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
                sourceId.eq(chapterSource.sourceId) and chapterId.eq(chapterSource.chapterId)
            }) {
                it[relevance] = chapterSource.relevance
            }
        }
    }
}