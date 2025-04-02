package newsref.app.model

import kotlinx.serialization.Serializable
import newsref.model.core.SourceType
import newsref.model.core.Relevance

@Serializable
data class ChapterPage(
    val chapterId: Long,
    val pageId: Long,
    val sourceType: SourceType,
    val textDistance: Float?,
    val relevance: Relevance?,
)