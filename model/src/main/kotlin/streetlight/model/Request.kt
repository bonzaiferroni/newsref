package streetlight.model

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int = 0,
    val eventId: Int = 0,
    val songId: Int = 0,
    val time: Long = 0L,
    val performed: Boolean = false,
    val notes: String = "",
)