package newsref.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageAuthor(
	val name: String,
	val url: String? = null,
)