package newsref.db.tables

import newsref.db.utils.*
import newsref.model.data.Chapter
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ChapterTable : LongIdTable("chapter") {
    val storyId = reference("story_id", StoryTable, ReferenceOption.CASCADE).index()
    val title = text("title")
    val narrative = text("narrative")
    val createdAt = datetime("created_at").index()
    val score = integer("score")
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
}