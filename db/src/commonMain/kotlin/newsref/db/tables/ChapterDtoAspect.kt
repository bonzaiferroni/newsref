package newsref.db.tables

import newsref.db.core.*
import newsref.db.utils.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.*

object ChapterDtoAspect : Aspect<ChapterDtoAspect>(ChapterTable) {
    val id = add(ChapterTable.id)
    val title = add(ChapterTable.title)
    val score = add(ChapterTable.score)
    val size = add(ChapterTable.size)
    val cohesion = add(ChapterTable.cohesion)
    val happenedAt = add(ChapterTable.happenedAt)
}

fun ResultRow.toChapterDto() = ChapterDto(
    id = this[ChapterTable.id].value,
    title = this[ChapterTable.title],
    score = this[ChapterTable.score],
    size = this[ChapterTable.size],
    cohesion = this[ChapterTable.cohesion],
    happenedAt = this[ChapterTable.happenedAt].toInstantUtc(),
)