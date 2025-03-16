package newsref.db.tables

import newsref.db.model.ChapterSource
import newsref.db.model.ChapterSourceInfo
import newsref.model.core.SourceType
import newsref.model.core.Relevance
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ChapterSourceTable : LongIdTable("chapter_source") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val sourceId = reference("source_id", PageTable, ReferenceOption.CASCADE).index()
    val relevanceHuddleId = reference("relevance_huddle_id", HuddleTable, ReferenceOption.SET_NULL).nullable().index()
    val type = enumeration<SourceType>("type")
    val distance = float("distance").nullable()
    val textDistance = float("text_distance").nullable()
    val timeDistance = float("time_distance").nullable()
    val linkDistance = float("link_distance").nullable()
    val relevance = enumeration<Relevance>("relevance").nullable()

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