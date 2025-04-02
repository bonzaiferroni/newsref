package newsref.db.model

import kotlinx.datetime.Instant
import newsref.db.core.CheckedUrl
import newsref.model.core.ArticleType
import newsref.model.core.NewsSection
import newsref.model.core.ContentType

data class Page(
    val id: Long = 0,
    val hostId: Int = 0,
    val url: CheckedUrl,
    val title: String? = null,
    val type: ContentType? = null,
    val score: Int? = null,
    val feedPosition: Int? = null,
    val thumbnail: String? = null,
    val imageUrl: String? = null,
    val embed: String? = null,
    val cachedWordCount: Int? = null,
    val okResponse: Boolean,

    // article
    val locationId: Int? = null,
    val headline: String? = null,
    val alternativeHeadline: String? = null,
    val description: String? = null,
    val cannonUrl: String? = null,
    val metaSection: String? = null,
    val keywords: List<String>? = null,
    val wordCount: Int? = null,
    val isFree: Boolean? = null,
    val language: String? = null,
    val commentCount: Int? = null,
    val summary: String? = null,
    val documentType: DocumentType? = null,
    val section: NewsSection? = null,
    val articleType: ArticleType? = null,

    val seenAt: Instant,
    val accessedAt: Instant? = null,
    val publishedAt: Instant? = null,
    val modifiedAt: Instant? = null,
) {
	val existedAt get() = publishedAt ?: seenAt
}
