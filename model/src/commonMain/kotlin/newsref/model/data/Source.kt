package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.dto.SourceCollection

data class Source(
	val id: Long = 0,
	val hostId: Int = 0,
	val noteId: Long? = null,
	val url: CheckedUrl,
	val title: String? = null,
	val type: SourceType? = null,
	val score: Int? = null,
	val feedPosition: Int? = null,
	val thumbnail: String? = null,
	val imageUrl: String? = null,
	val embed: String? = null,
	val contentCount: Int? = null,
	val okResponse: Boolean,
	val seenAt: Instant,
	val accessedAt: Instant? = null,
	val publishedAt: Instant? = null,
) {
	val existedAt get() = publishedAt ?: seenAt
}

data class SourceScore(
	val sourceId: Long,
	val score: Int,
	val scoredAt: Instant,
	val originId: Long?,
	val feedId: Int?,
)

@Serializable
data class SourceCache(
	val id: Int = 0,
	val sourceId: Long = 0,
	val score: Int,
	val createdAt: Instant,
	val json: SourceCollection,
)