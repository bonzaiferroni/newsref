package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ChapterAspect
import newsref.db.tables.ChapterTable
import newsref.db.tables.toChapter
import org.jetbrains.exposed.sql.SortOrder

class ChapterService : DbService() {

    suspend fun readChapter(chapterId: Long) = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.toChapter()
    }

    suspend fun readChapters(limit: Int = 100) = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .orderBy(ChapterTable.score, SortOrder.DESC)
            .limit(limit)
            .map { it.toChapter() }
    }
}