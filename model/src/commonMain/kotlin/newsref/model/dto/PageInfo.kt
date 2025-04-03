package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PageInfo(
    val pageId: Long,
    val url: String,
    val pageTitle: String?,
    val headline: String?,
    val description: String?,
    val hostCore: String,
    val hostName: String?,
    val hostLogo: String?,
    val wordCount: Int?,
    val metaSection: String?,
    val image: String?,
    val thumbnail: String?,
    val seenAt: Instant,
    val score: Int,
    val publishedAt: Instant?,
) {
	val existedAt get() = publishedAt ?: seenAt
}

@Serializable
data class PageCollection(
	val info: PageInfo,
	val authors: List<String>?,
	val inLinks: List<LinkCollection>,
	val outLinks: List<LinkCollection>,
	val scores: List<ScoreInfo>
)

@Serializable
data class LinkInfo(
	val originId: Long,
	val targetId: Long?,
	val url: String,
	val urlText: String,
	val textIndex: Int,
	val isExternal: Boolean,
	val context: String?,
	val originUrl: String,
	val hostName: String?,
	val hostCore: String,
	val headline: String?,
	val seenAt: Instant,
	val publishedAt: Instant?,
)

@Serializable
data class LinkCollection(
	val info: LinkInfo,
	val authors: List<CrawledAuthor>?,
)

@Serializable
data class ScoreInfo(
	val score: Int,
	val scoredAt: Instant,
)

@Serializable
data class NoteInfo(
	val userId: Long,
	val username: String,
	val subject: String,
	val body: String,
	val createdAt: Instant,
	val modifiedAt: Instant = Instant.DISTANT_PAST,
)