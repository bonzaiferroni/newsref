package newsref.db.tables

import newsref.db.model.ChapterSource
import newsref.db.model.ChapterSourceInfo
import newsref.db.model.Relevance
import newsref.db.model.NewsSourceType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

object ChapterSourceTable : LongIdTable("chapter_source") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val sourceId = reference("source_id", PageTable, ReferenceOption.CASCADE).index()
    // val relevance = text("relevance")
    // val contrast = text("contrast")
    val type = enumeration("type", NewsSourceType::class)
    val distance = float("distance").nullable()
    val textDistance = float("text_distance").nullable()
    val timeDistance = float("time_distance").nullable()
    val linkDistance = float("link_distance").nullable()
    val relevance = enumeration("relevance", Relevance::class).nullable()

    init {
        uniqueIndex(chapterId, sourceId)
    }
}

fun ResultRow.toChapterSource() = ChapterSource(
    id = this[ChapterSourceTable.id].value,
    chapterId = this[ChapterSourceTable.chapterId].value,
    sourceId = this[ChapterSourceTable.sourceId].value,
    // relevance = this[ChapterSourceTable.relevance],
    // contrast = this[ChapterSourceTable.contrast],
    type = this[ChapterSourceTable.type],
    distance = this[ChapterSourceTable.distance],
    textDistance = this[ChapterSourceTable.textDistance],
    timeDistance = this[ChapterSourceTable.timeDistance],
    linkDistance = this[ChapterSourceTable.linkDistance],
    relevance = this[ChapterSourceTable.relevance],
)

// chapter source info

internal fun ResultRow.toChapterSourceInfo() = ChapterSourceInfo(
    chapterSource = this.toChapterSource(),
    source = this.toSource(),
)

//internal fun SourceTable.notInChapter(): NotInSubQueryOp<EntityID<Long>> {
//    val subquery = ChapterSourceTable.select(sourceId).where { relevance.isNullOrNeq(Relevance.Irrelevant) }
//    return this.id.notInSubQuery(subQuery)
//}