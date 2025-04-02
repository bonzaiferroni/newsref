package newsref.db.model

import newsref.db.core.CheckedUrl

data class Link(
    val id: Long = 0,
    val pageId: Long = 0,
    val contentId: Long? = null,
    val leadId: Long? = null,
    val url: CheckedUrl,
    val text: String,
    val textIndex: Int,
    val isExternal: Boolean,
    // todo: add startIndex
)