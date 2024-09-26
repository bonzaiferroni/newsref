package newsref.model.data

import kotlinx.datetime.Instant

data class Source(
    val id: Long = 0,
    val outletId: Int = 0,
    val url: String = "",
    val type: SourceType = SourceType.UNKNOWN,
    val attemptedAt: Instant = Instant.DISTANT_PAST,
)
