package newsref.model.data

import kotlinx.datetime.Instant

data class Lead(
    val id: Int = 0,
    val sourceId: Long? = null,
    val url: String = "",
    val attemptCount: Int = 0,
    val attemptedAt: Instant? = null
)