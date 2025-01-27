package newsref.db.tables

import newsref.db.core.vector
import newsref.db.utils.*
import newsref.model.data.Chapter
import newsref.model.data.ChapterSource
import newsref.model.data.StorySourceType
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ChapterTable : LongIdTable("chapter") {
    val storyId = reference("story_id", StoryTable, ReferenceOption.CASCADE).index()
    val title = text("title")
    val narrative = text("narrative")
    val createdAt = datetime("created_at").index()
    val score = integer("score")
    val average = vector("average")
}

fun ResultRow.toChapter() = Chapter(
    id = this[ChapterTable.id].value,
    storyId = this[ChapterTable.storyId].value,
    title = this[ChapterTable.title],
    narrative = this[ChapterTable.narrative],
    createdAt = this[ChapterTable.createdAt].toInstantUtc(),
    score = this[ChapterTable.score],
)

// chapter source

object ChapterSourceTable : LongIdTable("chapter_source") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE).index()
    val relevance = text("relevance")
    val contrast = text("contrast")
    val type = enumeration("type", StorySourceType::class)
}

fun ResultRow.toChapterSource() = ChapterSource(
    id = this[ChapterSourceTable.id].value,
    chapterId = this[ChapterSourceTable.chapterId].value,
    sourceId = this[ChapterSourceTable.sourceId].value,
    relevance = this[ChapterSourceTable.relevance],
    contrast = this[ChapterSourceTable.contrast],
    type = this[ChapterSourceTable.type],
)