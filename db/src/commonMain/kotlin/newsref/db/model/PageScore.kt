package newsref.db.model

import kotlinx.datetime.Instant

data class PageScore(
    val pageId: Long,
    val score: Int,
    val scoredAt: Instant,
    val originId: Long?,
    val feedId: Int?,
)