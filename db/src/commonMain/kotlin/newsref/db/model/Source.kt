package newsref.db.model

import kotlinx.datetime.Instant
import newsref.db.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.dto.SourceDto

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