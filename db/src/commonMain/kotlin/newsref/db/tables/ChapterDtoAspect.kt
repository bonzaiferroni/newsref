package newsref.db.tables

import newsref.db.utils.toInstantUtc
import newsref.model.dto.ChapterDto
import org.jetbrains.exposed.sql.ResultRow

internal val chapterDtoColumns = listOf(
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

fun ResultRow.toChapterDto() = ChapterDto(
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