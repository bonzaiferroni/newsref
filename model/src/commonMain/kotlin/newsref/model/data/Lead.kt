package newsref.model.data

import kotlinx.datetime.Instant

data class Lead(
    val id: Long = 0,
    val sourceId: Long? = null,
    val feedId: Int? = null,
    val url: String = "",
    val headline: String? = null,
    val attemptCount: Int = 0,
    val attemptedAt: Instant? = null
)