package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.model.Chapter
import newsref.db.tables.*
import newsref.db.utils.since
import newsref.db.model.NewsSourceType
import newsref.db.utils.isAfter
import newsref.model.dto.ChapterDto
import newsref.model.dto.ChapterPackDto
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import kotlin.time.*

class ChapterDtoService : DbService() {

    suspend fun readTopChapters(start: Instant, limit: Int = 20) = dbQuery {
        ChapterDtoAspect
            .where { ChapterDtoAspect.happenedAt.isAfter(start) }
            .orderBy(ChapterDtoAspect.score, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            .map { toChapterPackDto(it.toChapterDto()) }
    }

    suspend fun readChapter(id: Long) = dbQuery {
        ChapterDtoAspect.where { ChapterDtoAspect.id.eq(id) }
            .firstOrNull()?.toChapterDto()?.let { toChapterPackDto(it) }
    }

    fun Transaction.toChapterPackDto(chapter: ChapterDto): ChapterPackDto {
        val sources = ChapterSourceTable.leftJoin(PageTable).leftJoin(HostTable)
            .select(SourceBitAspect.columns)
            .where {
                ChapterSourceTable.chapterId.eq(chapter.id) and
                        ChapterSourceTable.type.eq(NewsSourceType.Secondary)
            }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .limit(30)
            .map { it.toSourceBitDto() }
        return ChapterPackDto(chapter, sources)
    }
}