package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.model.Chapter
import newsref.db.utils.toInstantUtc
import org.jetbrains.exposed.sql.ResultRow

object ChapterAspect : Aspect<ChapterAspect, Chapter>(ChapterTable, ResultRow::toChapter) {
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
    averageAt = this[ChapterTable.happenedAt].toInstantUtc(),
)