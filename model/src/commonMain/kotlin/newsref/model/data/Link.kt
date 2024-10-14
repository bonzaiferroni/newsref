package newsref.model.data

import newsref.model.core.CheckedUrl

data class Link(
    val id: Long = 0,
    val sourceId: Long = 0,
    val contentId: Long = 0,
    val targetId: Long = 0,
    val url: CheckedUrl,
    val text: String,
)