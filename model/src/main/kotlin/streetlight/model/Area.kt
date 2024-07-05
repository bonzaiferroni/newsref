package streetlight.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Area(
    val id: Int = 0,
    val name: String = "",
    @SerialName("parent_id")
    val parentId: Int? = null,
)