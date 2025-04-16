package newsref.db.tables

import klutch.db.Aspect
import newsref.db.core.*
import newsref.model.data.ChapterPage
import org.jetbrains.exposed.sql.*

object ChapterPageDtoAspect : Aspect<ChapterPageDtoAspect, ChapterPage>(
    ChapterPageTable.leftJoin(PageTable),
    ResultRow::toChapterSourceDto
) {
    val pageColumns = add(PageDtoAspect.columns)

    val chapterId = add(ChapterPageTable.chapterId)
    val pageId = add(ChapterPageTable.pageId)
    val sourceType = add(ChapterPageTable.sourceType)
    val textDistance = add(ChapterPageTable.textDistance)
    val relevance = add(ChapterPageTable.relevance)
}

fun ResultRow.toChapterSourceDto() = ChapterPage(
    page = this.toPageDto(),

    chapterId = this[ChapterPageDtoAspect.chapterId].value,
    pageId = this[ChapterPageDtoAspect.pageId].value,
    sourceType = this[ChapterPageDtoAspect.sourceType],
    textDistance = this[ChapterPageDtoAspect.textDistance],
    relevance = this[ChapterPageDtoAspect.relevance],
)