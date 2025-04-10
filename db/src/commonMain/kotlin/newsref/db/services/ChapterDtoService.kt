package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.data.Chapter
import org.jetbrains.exposed.sql.*

class ChapterDtoService : DbService() {

    suspend fun readTopChapters(start: Instant, limit: Int = 20) = dbQuery {
        ChapterDtoAspect.read(ChapterDtoAspect.score, SortOrder.DESC_NULLS_LAST, limit) {
            it.happenedAt.greater(start) and it.title.isNotNull()
        }.map { it.addPages(30) }
    }

    suspend fun readChapter(id: Long) = dbQuery {
        ChapterDtoAspect.readFirst { it.id.eq(id) }?.addPages(500)
    }

    fun Chapter.addPages(limit: Int): Chapter {
        val pages = ChapterPageLiteAspect
            .where { it.chapterId.eq(this@addPages.id) }
            .orderBy(Pair(ChapterPageTable.sourceType, SortOrder.DESC), Pair(PageTable.score, SortOrder.DESC_NULLS_LAST))
            .limit(limit)
            .map { it.toChapterPageLite() }
        return this.copy(pages = pages)
    }

    suspend fun readChapterPage(chapterId: Long, pageId: Long) = dbQuery {
        ChapterPageDtoAspect.readFirst { it.chapterId.eq(chapterId) and it.pageId.eq(pageId) }
    }
}