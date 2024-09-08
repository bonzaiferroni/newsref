package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String? = null,
    val username: String = "",
    val email: String? = null,
    val roles: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val avatarUrl: String? = null,
    val venmo: String? = null,
)