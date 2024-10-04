package newsref.model.data

import newsref.model.core.CheckedUrl

data class Lead(
    val id: Long,
    val url: CheckedUrl,
    val targetId: Long? = null,
)