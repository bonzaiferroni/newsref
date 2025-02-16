package newsref.app.model

import kotlinx.datetime.Instant
import newsref.model.dto.ChapterDto

data class Chapter(
    val id: Long = 0,
    val storyId: Long? = null,
    val parentId: Long? = null,
    val title: String? = null,
    val summary: String? = null,
    val score: Int,
    val size: Int,
    val cohesion: Float,
    val storyDistance: Float?,
    val createdAt: Instant,
    val happenedAt: Instant,
)

fun ChapterDto.toChapter() = Chapter(
    id = this.id,
    storyId = this.storyId,
    parentId = this.parentId,
    title = this.title,
    summary = this.summary,
    score = this.score,
    size = this.size,
    cohesion = this.cohesion,
    storyDistance = this.storyDistance,
    createdAt = this.createdAt,
    happenedAt = this.happenedAt,
)