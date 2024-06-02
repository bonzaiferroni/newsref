package streetlight.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Performance(
    val id: Int = 0,
    @SerialName("user_id")
    val userId: Int = 0,
    val name: String = "",
)