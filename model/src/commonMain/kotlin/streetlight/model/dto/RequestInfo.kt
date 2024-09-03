package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class RequestInfo(
    val id: Int,
    val eventId: Int,
    val locationName: String,
    val songId: Int,
    val songName: String,
    val artist: String?,
    val notes: String,
    val requesterName: String? = null,
    val time: Long = 0L,
    val performed: Boolean = false,
)