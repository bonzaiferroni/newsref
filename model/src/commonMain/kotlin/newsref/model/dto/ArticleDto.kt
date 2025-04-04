package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import newsref.model.core.*

@Serializable
data class ArticleDto(
    val id: Long,
    val hostId: Int,
    val url: String,
    val headline: String?,
    val type: ContentType?,
    val score: Int?,
    val thumbnail: String?,
    val imageUrl: String?,
    val embed: String?,
    val wordCount: Int?,
    val summary: String?,
    val articleType: ArticleType?,
    val articleTypeHuddleId: Long?,
    val seenAt: Instant,
    val accessedAt: Instant?,
    val publishedAt: Instant?,
) {
}

@Serializable
data class PageBitDto(
    val id: Long,
    val hostId: Int,
    val url: String,
    val imageUrl: String?,
    val hostCore: String,
    val headline: String?,
    val score: Int,
    val feedPosition: Int?,
    val contentType: ContentType,
    val articleType: ArticleType,
    val newsSection: NewsSection,
    val existedAt: Instant,
)