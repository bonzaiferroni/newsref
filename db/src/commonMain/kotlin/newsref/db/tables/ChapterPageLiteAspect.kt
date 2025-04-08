package newsref.db.tables

import newsref.db.core.Aspect
import newsref.model.data.ChapterPageLite
import org.jetbrains.exposed.sql.ResultRow

object ChapterPageLiteAspect : Aspect<ChapterPageLiteAspect, ChapterPageLite>(
    ChapterPageTable.leftJoin(PageTable).leftJoin(HostTable),
    ResultRow::toChapterPageLite
) {
    val pageLiteColumns = add(PageLiteAspect.columns)

    val chapterId = add(ChapterPageTable.chapterId)
    val sourceType = add(ChapterPageTable.sourceType)
    val textDistance = add(ChapterPageTable.textDistance)
    val relevance = add(ChapterPageTable.relevance)
}

fun ResultRow.toChapterPageLite() = ChapterPageLite(
    page = this.toPageLite(),

    chapterId = this[ChapterPageLiteAspect.chapterId].value,
    pageId = this[PageLiteAspect.id].value,
    sourceType = this[ChapterPageLiteAspect.sourceType],
    textDistance = this[ChapterPageLiteAspect.textDistance],
    relevance = this[ChapterPageLiteAspect.relevance],
)