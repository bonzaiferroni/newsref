package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.since
import newsref.db.model.NewsSourceType
import newsref.model.dto.ChapterPackDto
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import kotlin.time.*

class ChapterDtoService : DbService() {

    suspend fun readTopChapters(duration: Duration, limit: Int = 20) = dbQuery {
        val chapters = ChapterDtoAspect
            .where { ChapterDtoAspect.happenedAt.since(duration) }
            .orderBy(ChapterDtoAspect.score, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            .map { it.toChapterDto() }
        chapters.map {
            val sources = ChapterSourceTable.leftJoin(SourceTable)
                .select(SourceDtoAspect.columns)
                .where {
                    ChapterSourceTable.chapterId.eq(it.id) and
                            ChapterSourceTable.type.eq(NewsSourceType.Secondary)
                }
                .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
                .limit(3)
                .map { it.toSourceDto() }
            ChapterPackDto(it, sources)
        }
    }
}