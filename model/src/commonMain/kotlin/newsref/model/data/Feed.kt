package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val id: Int,
    val url: String,
)