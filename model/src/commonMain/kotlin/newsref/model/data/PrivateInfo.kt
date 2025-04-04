package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class PrivateInfo(
    val email: String? = null,
    val name: String? = null,
)