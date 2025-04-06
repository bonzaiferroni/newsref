package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.data.Chapter
import newsref.model.data.ChapterPack
import org.jetbrains.exposed.sql.*

class ChapterDtoService : DbService() {

    suspend fun readTopChapters(start: Instant, limit: Int = 20) = dbQuery {
        ChapterDtoAspect.read(ChapterDtoAspect.score, SortOrder.DESC_NULLS_LAST, limit) {
            it.happenedAt.greater(start)
        }
            .map { toChapterPackDto(it, 30) }
    }

    suspend fun readChapter(id: Long) = dbQuery {
        ChapterDtoAspect.readFirst { it.id.eq(id) }?.let { toChapterPackDto(it, 500) }
    }

    fun Transaction.toChapterPackDto(chapter: Chapter, limit: Int): ChapterPack {
        val pageBits = ChapterPageTable.leftJoin(PageTable).leftJoin(HostTable)
            .select(PageBitAspect.columns)
            .where { ChapterPageTable.chapterId.eq(chapter.id) }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            .map { it.toPageBitDto() }
        return ChapterPack(chapter, pageBits)
    }

    suspend fun readChapterSource(chapterId: Long, pageId: Long) = dbQuery {
        ChapterPageDtoAspect.readFirst { it.chapterId.eq(chapterId) and it.pageId.eq(pageId) }
    }
}