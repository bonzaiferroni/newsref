package newsref.db.tables

import newsref.db.core.*
import newsref.db.utils.*
import newsref.model.data.Chapter
import org.jetbrains.exposed.sql.*

object ChapterDtoAspect : Aspect<ChapterDtoAspect, Chapter>(ChapterTable, ResultRow::toChapterDto) {
    val id = add(ChapterTable.id)
    val title = add(ChapterTable.title)
    val score = add(ChapterTable.score)
    val size = add(ChapterTable.size)
    val cohesion = add(ChapterTable.cohesion)
    val happenedAt = add(ChapterTable.happenedAt)
}

fun ResultRow.toChapterDto() = Chapter(
    pages = null,

    id = this[ChapterTable.id].value,
    title = this[ChapterTable.title],
    score = this[ChapterTable.score],
    size = this[ChapterTable.size],
    cohesion = this[ChapterTable.cohesion],
    averageAt = this[ChapterTable.happenedAt].toInstantUtc(),
)