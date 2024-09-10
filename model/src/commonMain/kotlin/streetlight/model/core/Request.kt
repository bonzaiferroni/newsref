package streetlight.model.core

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    override val id: Int = 0,
    val eventId: Int = 0,
    val songId: Int = 0,
    val time: Long = 0L,
    val performed: Boolean = false,
    val notes: String = "",
    val requesterName: String? = null,
) : IdModel