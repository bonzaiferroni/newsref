package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class CrawledAuthor(
	val name: String,
	val url: String? = null,
)