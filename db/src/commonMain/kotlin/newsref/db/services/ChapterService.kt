package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ChapterTable
import newsref.db.tables.chapterColumns
import newsref.db.tables.toChapter

class ChapterService : DbService() {

    suspend fun readChapter(chapterId: Long) = dbQuery {
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.toChapter()
    }
}