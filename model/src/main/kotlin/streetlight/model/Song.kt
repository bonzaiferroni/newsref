package streetlight.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String = "",
    val artist: String? = null,
    val data: String = "",
)