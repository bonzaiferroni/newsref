package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.utils.takeEllipsis

@Serializable
data class Page(
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
    val existedAt get() = publishedAt ?: seenAt
    val bestTitle get() = headline ?: "[${url.takeEllipsis(50)}]"
}