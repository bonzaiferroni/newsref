package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.since
import kotlin.time.*

class ChapterService : DbService() {

    suspend fun readChapters(duration: Duration) = dbQuery {
        ChapterTable.select(chapterColumns)
            .where { ChapterTable.happenedAt.since(duration) }
            .map { it.toChapter() }
    }
}