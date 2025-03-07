package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import newsref.model.core.*

@Serializable
data class SourceDto(
    val id: Long = 0,
    val url: String,
    val title: String? = null,
    val type: PageType? = null,
    val score: Int? = null,
    val thumbnail: String? = null,
    val imageUrl: String? = null,
    val embed: String? = null,
    val wordCount: Int? = null,
    val seenAt: Instant,
    val publishedAt: Instant? = null,
) {
}

@Serializable
data class SourceBitDto(
    val id: Long,
    val url: String,
    val imageUrl: String?,
    val hostCore: String,
    val title: String?,
    val score: Int,
    val pageType: PageType,
    val existedAt: Instant,
)