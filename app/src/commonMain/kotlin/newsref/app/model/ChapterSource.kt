package newsref.app.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.SourceType
import newsref.model.core.Relevance

@Serializable
data class ChapterSource(
    val chapterId: Long,
    val pageId: Long,
    val sourceType: SourceType,
    val textDistance: Float?,
    val relevance: Relevance?,
)