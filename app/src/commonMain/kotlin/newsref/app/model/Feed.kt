package newsref.app.model

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import newsref.model.core.*
import newsref.model.dto.*

@Serializable
data class Feed(
    val id: Int,
    val url: String,
)