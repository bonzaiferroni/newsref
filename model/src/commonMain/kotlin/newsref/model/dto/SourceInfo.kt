package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.SourceType
import newsref.model.data.Article

@Serializable
data class SourceInfo(
	val id: Long = 0,
	val url: String,
	val type: SourceType?,
	val leadTitle: String?,
	val seenAt: Instant,
	val hostCore: String,
	val hostName: String? = null,
	val hostLogo: String? = null,
	val citationCount: Int? = null,
	val article: Article? = null,
	val inLinks: List<LinkInfo>? = null,
	val outLinks: List<LinkInfo>? = null,
) {
	val bestTitle get() = article?.headline ?: leadTitle ?: url
}

@Serializable
data class LinkInfo(
	val targetId: Long? = null,
	val sourceId: Long = 0,
	val url: String,
	val urlText: String,
	val context: String?,
	val sourceUrl: String,
	val seenAt: Instant
)