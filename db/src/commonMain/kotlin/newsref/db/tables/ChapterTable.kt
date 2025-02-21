package newsref.db.tables

import newsref.db.core.*
import newsref.db.utils.*
import newsref.db.model.*
import newsref.model.dto.*
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

object ChapterAspect : Aspect(ChapterTable) {
    val id = add(ChapterTable.id)
    val storyId = add(ChapterTable.storyId)
    val parentId = add(ChapterTable.parentId)
    val title = add(ChapterTable.title)
    val summary = add(ChapterTable.summary)
    val score = add(ChapterTable.score)
    val size = add(ChapterTable.size)
    val cohesion = add(ChapterTable.cohesion)
    val storyDistance = add(ChapterTable.storyDistance)
    val createdAt = add(ChapterTable.createdAt)
    val happenedAt = add(ChapterTable.happenedAt)
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