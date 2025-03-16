package newsref.db.tables

import newsref.db.core.*
import newsref.db.utils.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.*

object ChapterSourceDtoAspect : Aspect<ChapterSourceDtoAspect, ChapterSourceDto>(
    ChapterSourceTable,
    ResultRow::toChapterSourceDto
) {
    val chapterId = add(ChapterSourceTable.chapterId)
    val pageId = add(ChapterSourceTable.sourceId)
    val sourceType = add(ChapterSourceTable.type)
    val textDistance = add(ChapterSourceTable.textDistance)
    val relevance = add(ChapterSourceTable.relevance)
}

fun ResultRow.toChapterSourceDto() = ChapterSourceDto(
    chapterId = this[ChapterSourceDtoAspect.chapterId].value,
    pageId = this[ChapterSourceDtoAspect.pageId].value,
    sourceType = this[ChapterSourceDtoAspect.sourceType],
    textDistance = this[ChapterSourceDtoAspect.textDistance],
    relevance = this[ChapterSourceDtoAspect.relevance],
)