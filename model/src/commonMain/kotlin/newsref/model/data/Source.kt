package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.dto.SourceInfo

data class Source(
	val id: Long = 0,
	val hostId: Int = 0,
	val url: CheckedUrl,
	val score: Int? = null,
	val type: SourceType? = null,
	val seenAt: Instant,
)

data class SourceScore(
	val sourceId: Long,
	val score: Int,
	val scoredAt: Instant
)

@Serializable
data class FeedSource(
	val id: Int = 0,
	val sourceId: Long = 0,
	val checkedAt: Instant,
	val json: SourceInfo,
)