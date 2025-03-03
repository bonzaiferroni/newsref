package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.model.*
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.*
import kotlin.time.*

class ChapterDtoService : DbService() {

    suspend fun readTopChapters(start: Instant, limit: Int = 20) = dbQuery {
        ChapterDtoAspect
            .where { it.happenedAt.isAfter(start) }
            .orderBy(ChapterDtoAspect.score, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            .map { toChapterPackDto(it.toChapterDto()) }
    }

    suspend fun readChapter(id: Long) = dbQuery {
        ChapterDtoAspect.where { it.id.eq(id) }
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

    suspend fun readChapterSource(chapterId: Long, pageId: Long) = dbQuery {
        ChapterSourceDtoAspect.where { it.chapterId.eq(chapterId) }
    }
}