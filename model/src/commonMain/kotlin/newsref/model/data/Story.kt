package newsref.model.data

import kotlinx.datetime.Instant

data class Story(
    val id: Long,
    val vectorModelId: Int,
    val narratorId: Int,
    val title: String,
    val intro: String,
)

data class Chapter(
    val id: Long = 0,
    val storyId: Long = 0,
    val title: String,
    val narrative: String? = null,
    val createdAt: Instant,
    val happenedAt: Instant,
    val score: Int,
)

data class ChapterSource(
    val id: Long,
    val chapterId: Long,
    val sourceId: Long,
    val relevance: String,
    val contrast: String,
    val type: StorySourceType,
)

data class Narrator(
    val id: Int = 0,
    val vectorModelId: Int = 0,
    val name: String,
    val bio: String,
    val chatModelUrl: String,
)

enum class StorySourceType {
    Primary,
    Secondary,
}