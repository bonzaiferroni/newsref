package newsref.db.tables

import newsref.db.core.vector
import newsref.db.utils.*
import newsref.db.model.Chapter
import newsref.db.model.ChapterSource
import newsref.db.model.ChapterSourceInfo
import newsref.db.model.ChapterSourceType
import newsref.model.dto.ChapterDto
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

internal fun InsertStatement<*>.fromModel(chapter: Chapter) {
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