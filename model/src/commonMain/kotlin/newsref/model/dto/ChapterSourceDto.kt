package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.*

@Serializable
data class ChapterSourceDto(
    val chapterId: Long,
    val pageId: Long,
    val sourceType: SourceType,
    val textDistance: Float?,
    val relevance: Relevance?,
)

@Serializable
data class ChapterSourcePackDto(
    val source: ChapterSourceDto,
    val chapter: ChapterPackDto
)