package newsref.db.tables

import newsref.db.model.ChapterPage
import newsref.db.model.ChapterPageInfo
import newsref.model.data.SourceType
import newsref.model.data.Relevance
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

object ChapterPageTable : LongIdTable("chapter_page") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
    val relevanceHuddleId = reference("relevance_huddle_id", HuddleTable, ReferenceOption.SET_NULL).nullable().index()
    val sourceType = enumeration<SourceType>("type")
    val distance = float("distance").nullable()
    val textDistance = float("text_distance").nullable()
    val timeDistance = float("time_distance").nullable()
    val linkDistance = float("link_distance").nullable()
    val relevance = enumeration<Relevance>("relevance").nullable()

    init {
        uniqueIndex(chapterId, pageId)
    }
}

fun ResultRow.toChapterSource() = ChapterPage(
    id = this[ChapterPageTable.id].value,
    chapterId = this[ChapterPageTable.chapterId].value,
    pageId = this[ChapterPageTable.pageId].value,
    // relevance = this[ChapterSourceTable.relevance],
    // contrast = this[ChapterSourceTable.contrast],
    type = this[ChapterPageTable.sourceType],
    distance = this[ChapterPageTable.distance],
    textDistance = this[ChapterPageTable.textDistance],
    timeDistance = this[ChapterPageTable.timeDistance],
    linkDistance = this[ChapterPageTable.linkDistance],
    relevance = this[ChapterPageTable.relevance],
)

// chapter source info

internal fun ResultRow.toChapterSourceInfo() = ChapterPageInfo(
    chapterPage = this.toChapterSource(),
    page = this.toPage(),
)