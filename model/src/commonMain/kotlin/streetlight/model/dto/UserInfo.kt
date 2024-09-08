package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val roles: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val avatarUrl: String? = null,
    val venmo: String? = null,
)