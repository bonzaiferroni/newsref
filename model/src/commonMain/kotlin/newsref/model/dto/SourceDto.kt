package newsref.model.dto

import kotlinx.datetime.Instant
import newsref.model.core.SourceType

data class SourceDto(
    val id: Long = 0,
    val hostId: Int = 0,
    val noteId: Long? = null,
    val url: String,
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
}