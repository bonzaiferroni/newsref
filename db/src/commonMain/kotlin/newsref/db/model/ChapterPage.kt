package newsref.db.model

import newsref.model.data.Relevance
import newsref.model.data.SourceType

data class ChapterPage(
    val id: Long = 0,
    val chapterId: Long = 0,
    val pageId: Long,
    // val relevance: String? = null,
    // val contrast: String? = null,
    val type: SourceType,
    val distance: Float?,
    val textDistance: Float?,
    val timeDistance: Float?,
    val linkDistance: Float?,
    val relevance: Relevance? = null,
)