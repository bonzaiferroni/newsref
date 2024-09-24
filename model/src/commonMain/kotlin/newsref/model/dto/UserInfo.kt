package newsref.model.dto

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

val UserInfo.isAdmin: Boolean
    get() = roles.contains("admin")

val UserInfo.isUser: Boolean
    get() = roles.contains("user")