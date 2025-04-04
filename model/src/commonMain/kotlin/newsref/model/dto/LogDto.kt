package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LogDto(
    val id: Long = 0,
    val pageId: Long? = null,
    val origin: String,
    val subject: String,
    val message: String,
    val time: Instant,
)

@Serializable
data class LogKey(
    val pageId: Long? = null
)
// maybe: use dto pattern