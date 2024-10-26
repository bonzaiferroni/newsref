package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType

data class Source(
	val id: Long = 0,
	val hostId: Int = 0,
	val url: CheckedUrl,
	val title: String? = null,
	val type: SourceType? = null,
	val seenAt: Instant,
)

data class SourceScore(
	val sourceId: Long,
	val score: Int,
	val scoredAt: Instant
)