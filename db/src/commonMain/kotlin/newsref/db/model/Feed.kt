package newsref.db.model

import kotlinx.datetime.Instant
import newsref.db.core.Url

data class Feed(
    val id: Int,
    val url: Url,
    val selector: String?,
    val external: Boolean,
    val trackPosition: Boolean,
    val linkCount: Int = 0,
    val debug: Boolean,
    val disabled: Boolean = false,
    val note: String = "",
    val createdAt: Instant,
    val checkAt: Instant,
)

data class FeedPage(
    val id: Long = 0,
    val feedId: Int,
    val pageId: Long,
    val position: Int,
)