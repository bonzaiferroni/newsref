package newsref.db.tables

import newsref.db.core.*
import newsref.model.data.ChapterPage
import org.jetbrains.exposed.sql.*

object ChapterPageDtoAspect : Aspect<ChapterPageDtoAspect, ChapterPage>(
    ChapterPageTable,
    ResultRow::toChapterSourceDto
) {
    val chapterId = add(ChapterPageTable.chapterId)
    val pageId = add(ChapterPageTable.pageId)
    val sourceType = add(ChapterPageTable.type)
    val textDistance = add(ChapterPageTable.textDistance)
    val relevance = add(ChapterPageTable.relevance)
}

fun ResultRow.toChapterSourceDto() = ChapterPage(
    chapterId = this[ChapterPageDtoAspect.chapterId].value,
    pageId = this[ChapterPageDtoAspect.pageId].value,
    sourceType = this[ChapterPageDtoAspect.sourceType],
    textDistance = this[ChapterPageDtoAspect.textDistance],
    relevance = this[ChapterPageDtoAspect.relevance],
)