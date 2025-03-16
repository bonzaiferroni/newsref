package newsref.app.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.ArticleType
import newsref.model.core.PageType
import newsref.model.utils.takeEllipsis

@Serializable
data class Article(
    val pageId: Long,
    val hostId: Int,
    val url: String,
    val headline: String?,
    val type: PageType?,
    val score: Int?,
    val thumbnail: String?,
    val imageUrl: String?,
    val embed: String?,
    val wordCount: Int?,
    val summary: String?,
    val articleType: ArticleType?,
    val seenAt: Instant,
    val accessedAt: Instant?,
    val publishedAt: Instant?,
) {
    val existedAt get() = publishedAt ?: seenAt
    val bestTitle get() = headline ?: "[${url.takeEllipsis(50)}]"
}