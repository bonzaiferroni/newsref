package newsref.db.tables

import klutch.db.vector
import klutch.utils.toLocalDateTimeUtc
import newsref.db.model.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.statements.InsertStatement

object ChapterTable : LongIdTable("chapter") {
    val storyId = reference("story_id", StoryTable, ReferenceOption.SET_NULL).nullable().index()
    val parentId = reference("parent_id", ChapterTable, ReferenceOption.SET_NULL).nullable().index()
    val titleHuddleId = reference("title_huddle_id", HuddleTable, ReferenceOption.SET_NULL).nullable()
    val locationId = reference("location_id", LocationTable, ReferenceOption.SET_NULL).nullable()
    val title = text("title").nullable()
    val summary = text("summary").nullable()
    val score = integer("score")
    val size = integer("size")
    val cohesion = float("cohesion")
    val storyDistance = float("story_distance").nullable()
    val level = integer("level").default(0)
    val createdAt = datetime("created_at").index()
    val happenedAt = datetime("happened_at").index()
    val vector = vector("vector", 768)
}

internal fun InsertStatement<*>.fromModel(chapter: Chapter) {
    this[ChapterTable.title] = chapter.title
    this[ChapterTable.summary] = chapter.summary
    this[ChapterTable.score] = chapter.score
    this[ChapterTable.size] = chapter.size
    this[ChapterTable.cohesion] = chapter.cohesion
    this[ChapterTable.createdAt] = chapter.createdAt.toLocalDateTimeUtc()
    this[ChapterTable.happenedAt] = chapter.averageAt.toLocalDateTimeUtc()
}