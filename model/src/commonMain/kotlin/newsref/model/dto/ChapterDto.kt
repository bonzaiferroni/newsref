package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChapterDto(
    val id: Long = 0,
    val title: String? = null,
    val score: Int,
    val size: Int,
    val cohesion: Float,
    val happenedAt: Instant,
)