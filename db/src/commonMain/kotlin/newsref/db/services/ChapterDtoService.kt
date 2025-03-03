package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.*

class ChapterDtoService : DbService() {

    suspend fun readTopChapters(start: Instant, limit: Int = 20) = dbQuery {
        ChapterDtoAspect.read(ChapterDtoAspect.score, SortOrder.DESC_NULLS_LAST, limit) {
            it.happenedAt.isAfter(start)
        }
            .map { toChapterPackDto(it) }
    }

    suspend fun readChapter(id: Long) = dbQuery {
        ChapterDtoAspect.readFirst { it.id.eq(id) }?.let { toChapterPackDto(it) }
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
        val chapterSourceDto = ChapterSourceDtoAspect.readFirst {
            it.chapterId.eq(chapterId) and it.pageId.eq(pageId)
        } ?: return@dbQuery
        val chapterPackDto = readChapter(chapterId)!!
        ChapterSourcePackDto(chapterSourceDto, chapterPackDto)
    }
}