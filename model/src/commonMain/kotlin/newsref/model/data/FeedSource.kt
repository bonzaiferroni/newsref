package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FeedSource(
	val id: Int = 0,
	val sourceId: Long = 0,
	val url: String,
	val headline: String?,
	val hostCore: String,
	val hostName: String?,
	val hostLogo: String?,
	val wordCount: Int?,
	val section: String?,
	val thumbnail: String?,
	val seenAt: Instant,
	val latestScore: Int,
	val publishedAt: Instant?,
	val authors: List<String>?,
	val inLinks: List<FeedSourceLink>,
	val scores: List<FeedSourceScore>
)

@Serializable
data class FeedSourceLink(
	val url: String,
	val urlText: String,
	val context: String?,
	val sourceUrl: String,
	val hostName: String?,
	val headline: String?,
	val seenAt: Instant,
	val publishedAt: Instant?,
)

@Serializable
data class FeedSourceScore(
	val score: Int,
	val scoredAt: Instant,
)