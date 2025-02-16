package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.since
import newsref.model.data.ChapterSourceType
import newsref.model.dto.ChapterDto
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import kotlin.time.*

class ChapterService : DbService() {

    suspend fun readTopChapters(duration: Duration, limit: Int = 20) = dbQuery {
        val chapters = ChapterTable.select(chapterColumns)
            .where { ChapterTable.happenedAt.since(duration) }
            .orderBy(ChapterTable.score, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            .map { it.toChapter() }
        chapters.map {
            val sources = ChapterSourceTable.leftJoin(SourceTable).select(SourceTable.columns)
                .where {
                    ChapterSourceTable.chapterId.eq(it.id) and
                            ChapterSourceTable.type.eq(ChapterSourceType.Secondary)
                }
                .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
                .limit(3)
                .map { it.toSource() }
            ChapterDto(it, sources)
        }
    }
}