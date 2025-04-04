package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class ChapterPage(
    val chapterId: Long,
    val pageId: Long,
    val sourceType: SourceType,
    val textDistance: Float?,
    val relevance: Relevance?,
)