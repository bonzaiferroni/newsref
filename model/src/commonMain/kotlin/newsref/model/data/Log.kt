package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Log(
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