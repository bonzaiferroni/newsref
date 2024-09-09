package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val username: String = "",
    val roles: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val avatarUrl: String? = null,
    val venmo: String? = null,
)