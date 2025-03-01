package newsref.app.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.dto.ChapterDto

@Serializable
data class Chapter(
    val id: Long = 0,
    val title: String? = null,
    val score: Int,
    val size: Int,
    val cohesion: Float,
    val happenedAt: Instant,
)

fun ChapterDto.toChapter() = Chapter(
    id = this.id,
    title = this.title,
    score = this.score,
    size = this.size,
    cohesion = this.cohesion,
    happenedAt = this.happenedAt,
)