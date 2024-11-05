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
	val title: String? = null,
	val score: Int? = null,
	val type: SourceType? = null,
	val thumbnail: String? = null,
	val imageUrl: String? = null,
	val embed: String? = null,
	val seenAt: Instant,
	val accessedAt: Instant? = null,
	val publishedAt: Instant? = null,
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
	val score: Int,
	val createdAt: Instant,
	val json: SourceInfo,
)