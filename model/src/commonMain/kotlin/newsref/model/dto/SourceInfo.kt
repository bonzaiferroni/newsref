package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.SourceType
import newsref.model.data.Article
import newsref.model.data.Host

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
	val citationCount: Int,
	val article: Article? = null,
)

@Serializable
data class CitationInfo(
	val targetId: Long = 0,
	val sourceId: Long = 0,
	val url: String,
	val urlText: String,
	val contentId: Long?,
)