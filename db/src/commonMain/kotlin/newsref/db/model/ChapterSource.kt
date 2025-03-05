package newsref.db.model

import newsref.model.core.*

data class ChapterSource(
    val id: Long = 0,
    val chapterId: Long = 0,
    val sourceId: Long,
    // val relevance: String? = null,
    // val contrast: String? = null,
    val type: SourceType,
    val distance: Float?,
    val textDistance: Float?,
    val timeDistance: Float?,
    val linkDistance: Float?,
    val relevance: Relevance? = null,
)