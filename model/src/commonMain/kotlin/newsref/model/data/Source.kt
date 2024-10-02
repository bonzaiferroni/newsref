package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.CheckedUrl

data class Source(
    val id: Long = 0,
    val outletId: Int = 0,
    val url: CheckedUrl,
    val leadTitle: String? = null,
    val type: SourceType = SourceType.UNKNOWN,
    val attemptedAt: Instant = Instant.DISTANT_PAST,
)
