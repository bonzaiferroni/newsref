package streetlight.model.core

import kotlinx.serialization.Serializable

@Serializable
data class Area(
    override val id: Int = 0,
    val parentId: Int? = null,
    val name: String = "",
) : IdModel