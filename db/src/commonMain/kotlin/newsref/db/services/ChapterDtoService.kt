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
        }.map { it.addCollections(30) }
    }

    suspend fun readChapter(id: Long) = dbQuery {
        ChapterDtoAspect.readFirst { it.id.eq(id) }?.addCollections(500)
    }

    fun Chapter.addCollections(limit: Int): Chapter {
        val pages = ChapterPageLiteAspect
            .where { it.chapterId.eq(this@addCollections.id) }
            .orderBy(Pair(ChapterPageTable.sourceType, SortOrder.DESC), Pair(PageTable.score, SortOrder.DESC_NULLS_LAST))
            .limit(limit)
            .map { it.toChapterPageLite() }
        return this.copy(pages = pages)
    }

    suspend fun readChapterPage(chapterId: Long, pageId: Long) = dbQuery {
        ChapterPageDtoAspect.readFirst { it.chapterId.eq(chapterId) and it.pageId.eq(pageId) }
    }

    suspend fun readChapterPersons(chapterId: Long) = dbQuery {
        ChapterPersonAspect.read { ChapterPersonAspect.chapterId.eq(chapterId) }
    }
}