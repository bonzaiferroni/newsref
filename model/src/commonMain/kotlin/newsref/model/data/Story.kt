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
    val id: Long,
    val storyId: Long,
    val title: String,
    val narrative: String,
    val createdAt: Instant,
    val score: Int,
)

data class ChapterSource(
    val id: Long,
    val chapterId: Long,
    val relevance: String,
    val contrast: String,
)

data class Narrator(
    val id: Int,
    val name: String,
    val bio: String,
)