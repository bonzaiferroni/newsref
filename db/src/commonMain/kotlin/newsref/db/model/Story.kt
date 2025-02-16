package newsref.db.model

import kotlinx.datetime.Instant

data class Story(
    val id: Long,
    val title: String?,
    val size: Int,
    val score: Int,
    val coherence: Float,
    val happenedAt: Instant
)