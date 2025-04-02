package newsref.model.dto

import kotlinx.serialization.Serializable
import newsref.model.core.*

@Serializable
data class ChapterPageDto(
    val chapterId: Long,
    val pageId: Long,
    val sourceType: SourceType,
    val textDistance: Float?,
    val relevance: Relevance?,
)

@Serializable
data class ChapterPagePackDto(
    val page: ChapterPageDto,
    val chapter: ChapterPackDto
)