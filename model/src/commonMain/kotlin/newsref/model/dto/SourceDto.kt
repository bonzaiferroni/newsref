package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.SourceType

@Serializable
data class SourceDto(
    val id: Long = 0,
    val url: String,
    val title: String? = null,
    val type: SourceType? = null,
    val score: Int? = null,
    val thumbnail: String? = null,
    val imageUrl: String? = null,
    val embed: String? = null,
    val wordCount: Int? = null,
    val seenAt: Instant,
    val publishedAt: Instant? = null,
) {
}