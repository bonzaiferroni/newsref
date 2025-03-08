package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import newsref.model.core.*

@Serializable
data class FeedDto(
    val id: Int,
    val url: String,
)