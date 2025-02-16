package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.ChapterDto
import org.jetbrains.exposed.sql.ResultRow

object ChapterDtoAspect : Aspect(ChapterTable) {
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