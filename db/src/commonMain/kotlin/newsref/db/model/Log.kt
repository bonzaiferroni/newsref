package newsref.db.model

import kotlinx.datetime.Instant

data class Log(
    val id: Long = 0,
    val pageId: Long? = null,
    val origin: String,
    val subject: String,
    val message: String,
    val time: Instant,
)