package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChapterDto(
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