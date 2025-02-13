package newsref.db.tables

import newsref.db.core.vector
import newsref.db.utils.*
import newsref.model.data.Chapter
import newsref.model.data.ChapterSource
import newsref.model.data.ChapterSourceInfo
import newsref.model.data.ChapterSourceType
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.statements.InsertStatement

object ChapterTable : LongIdTable("chapter") {
    val storyId = reference("story_id", StoryTable, ReferenceOption.SET_NULL).nullable().index()
    val parentId = reference("parent_id", ChapterTable, ReferenceOption.SET_NULL).nullable().index()
    val title = text("title").nullable()
    val summary = text("summary").nullable()
    val score = integer("score")
    val size = integer("size")
    val cohesion = float("cohesion")
    val storyDistance = float("story_distance").nullable()
    val createdAt = datetime("created_at").index()
    val happenedAt = datetime("happened_at").index()
    val vector = vector("vector", 1536)
}

fun ResultRow.toChapter() = Chapter(
    id = this[ChapterTable.id].value,
    storyId = this[ChapterTable.storyId]?.value,
    title = this[ChapterTable.title],
    summary = this[ChapterTable.summary],
    score = this[ChapterTable.score],
    size = this[ChapterTable.size],
    cohesion = this[ChapterTable.cohesion],
    storyDistance = this[ChapterTable.storyDistance],
    createdAt = this[ChapterTable.createdAt].toInstantUtc(),
    happenedAt = this[ChapterTable.happenedAt].toInstantUtc(),
)

internal fun InsertStatement<*>.fromData(chapter: Chapter) {
    this[ChapterTable.title] = chapter.title
    this[ChapterTable.summary] = chapter.summary
    this[ChapterTable.score] = chapter.score
    this[ChapterTable.size] = chapter.size
    this[ChapterTable.cohesion] = chapter.cohesion
    this[ChapterTable.createdAt] = chapter.createdAt.toLocalDateTimeUtc()
    this[ChapterTable.happenedAt] = chapter.happenedAt.toLocalDateTimeUtc()
}

internal val chapterColumns = listOf(
    ChapterTable.id,
    ChapterTable.storyId,
    ChapterTable.parentId,
    ChapterTable.title,
    ChapterTable.summary,
    ChapterTable.score,
    ChapterTable.size,
    ChapterTable.cohesion,
    ChapterTable.storyDistance,
    ChapterTable.createdAt,
    ChapterTable.happenedAt,
)

// chapter source

object ChapterSourceTable : LongIdTable("chapter_source") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE).index()
    // val relevance = text("relevance")
    // val contrast = text("contrast")
    val type = enumeration("type", ChapterSourceType::class)
    val distance = float("distance").nullable()
    val textDistance = float("text_distance").nullable()
    val timeDistance = float("time_distance").nullable()
    val linkDistance = float("link_distance").nullable()
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
)

// chapter source info

fun ResultRow.toChapterSourceInfo() = ChapterSourceInfo(
    chapterSource = this.toChapterSource(),
    source = this.toSource(),
)