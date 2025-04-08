package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.*

@Serializable
data class PageLite(
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