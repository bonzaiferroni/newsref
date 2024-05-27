package streetlight.model

import kotlinx.serialization.Serializable

@Serializable
data class Area(
    val id: Int = 0,
    val name: String = "",
    val parentId: Int? = null,
)