package newsref.model.data

import com.eygraber.uri.Url
import kotlinx.datetime.Instant

data class Source(
    val id: Long = 0,
    val outletId: Int = 0,
    val url: Url,
    val leadTitle: String? = null,
    val type: SourceType = SourceType.UNKNOWN,
    val attemptedAt: Instant = Instant.DISTANT_PAST,
)
