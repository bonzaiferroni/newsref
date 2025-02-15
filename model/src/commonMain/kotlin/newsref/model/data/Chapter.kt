package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
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

data class ChapterSource(
    val id: Long = 0,
    val chapterId: Long = 0,
    val sourceId: Long,
    // val relevance: String? = null,
    // val contrast: String? = null,
    val type: ChapterSourceType,
    val distance: Float?,
    val textDistance: Float?,
    val timeDistance: Float?,
    val linkDistance: Float?,
)

data class ChapterSourceInfo(
    val chapterSource: ChapterSource,
    val source: Source,
)

data class Narrator(
    val id: Int = 0,
    val vectorModelId: Int = 0,
    val name: String,
    val bio: String,
    val chatModelUrl: String,
)

enum class ChapterSourceType {
    Primary,
    Secondary,
}