package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SourceInfo(
	val sourceId: Long,
	val url: String,
	val pageTitle: String?,
	val headline: String?,
	val description: String?,
	val hostCore: String,
	val hostName: String?,
	val hostLogo: String?,
	val wordCount: Int?,
	val section: String?,
	val image: String?,
	val thumbnail: String?,
	val note: String?,
	val seenAt: Instant,
	val score: Int,
	val publishedAt: Instant?,
)

@Serializable
data class SourceCollection(
	val info: SourceInfo,
	val authors: List<String>?,
	val inLinks: List<LinkCollection>,
	val outLinks: List<LinkCollection>,
	val notes: List<NoteInfo>,
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
	val authors: List<PageAuthor>?,
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