package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val username: String = "",
    val roles: String = "",
    val avatarUrl: String? = null,
    val venmo: String? = null,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST,
)

val UserInfo.isAdmin: Boolean
    get() = roles.contains("admin")

val UserInfo.isUser: Boolean
    get() = roles.contains("user")