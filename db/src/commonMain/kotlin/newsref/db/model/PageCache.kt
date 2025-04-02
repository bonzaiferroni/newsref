package newsref.db.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.dto.PageCollection

@Serializable
data class PageCache(
    val id: Int = 0,
    val pageId: Long = 0,
    val score: Int,
    val createdAt: Instant,
    val json: PageCollection,
)