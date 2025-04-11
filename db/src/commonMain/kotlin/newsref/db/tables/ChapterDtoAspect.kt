package newsref.db.tables

import newsref.db.core.*
import newsref.db.services.toGeoPoint
import newsref.db.utils.*
import newsref.model.data.Chapter
import newsref.model.data.Location
import org.jetbrains.exposed.sql.*

object ChapterDtoAspect : Aspect<ChapterDtoAspect, Chapter>(
    ChapterTable.leftJoin(LocationTable),
    ResultRow::toChapterDto
) {
    val locationColumns = add(LocationTable.columns)

    val id = add(ChapterTable.id)
    val title = add(ChapterTable.title)
    val score = add(ChapterTable.score)
    val size = add(ChapterTable.size)
    val cohesion = add(ChapterTable.cohesion)
    val level = add(ChapterTable.level)
    val happenedAt = add(ChapterTable.happenedAt)
}

fun ResultRow.toChapterDto() = Chapter(
    pages = null,
    location = this.takeIf { this.getOrNull(LocationTable.id) != null }?.toLocationDto(),

    id = this[ChapterTable.id].value,
    title = this[ChapterTable.title],
    score = this[ChapterTable.score],
    size = this[ChapterTable.size],
    cohesion = this[ChapterTable.cohesion],
    level = this[ChapterTable.level],
    averageAt = this[ChapterTable.happenedAt].toInstantUtc(),
)

fun ResultRow.toLocationDto() = Location(
    id = this[LocationTable.id].value,
    name = this[LocationTable.name],
    geoPoint = this[LocationTable.geoPoint].toGeoPoint(),
    northEast = this[LocationTable.northEast].toGeoPoint(),
    southWest = this[LocationTable.southWest].toGeoPoint(),
)